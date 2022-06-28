package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.net.MailTo;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.google.android.exoplayer2.C;
import com.google.firebase.messaging.Constants;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FillLastLinearLayoutManager;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.AboutPremiumView;
import org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.PremiumNotAvailableBottomSheet;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanBrowser;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes4.dex */
public class PremiumPreviewFragment extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int PREMIUM_FEATURE_ADS = 3;
    public static final int PREMIUM_FEATURE_ADVANCED_CHAT_MANAGEMENT = 9;
    public static final int PREMIUM_FEATURE_ANIMATED_AVATARS = 7;
    public static final int PREMIUM_FEATURE_APPLICATION_ICONS = 10;
    public static final int PREMIUM_FEATURE_DOWNLOAD_SPEED = 2;
    public static final int PREMIUM_FEATURE_LIMITS = 0;
    public static final int PREMIUM_FEATURE_PROFILE_BADGE = 6;
    public static final int PREMIUM_FEATURE_REACTIONS = 4;
    public static final int PREMIUM_FEATURE_STICKERS = 5;
    public static final int PREMIUM_FEATURE_UPLOAD_LIMIT = 1;
    public static final int PREMIUM_FEATURE_VOICE_TO_TEXT = 8;
    BackgroundView backgroundView;
    private FrameLayout buttonContainer;
    private View buttonDivider;
    private FrameLayout contentView;
    private int currentYOffset;
    PremiumFeatureCell dummyCell;
    int featuresEndRow;
    int featuresStartRow;
    private int firstViewHeight;
    private boolean forcePremium;
    final Canvas gradientCanvas;
    final Bitmap gradientTextureBitmap;
    int helpUsRow;
    boolean inc;
    private boolean isDialogVisible;
    boolean isLandscapeMode;
    int lastPaddingRow;
    FillLastLinearLayoutManager layoutManager;
    RecyclerListView listView;
    int paddingRow;
    StarParticlesView particlesView;
    private PremiumButtonView premiumButtonView;
    int privacyRow;
    float progress;
    float progressToFull;
    int rowCount;
    int sectionRow;
    FrameLayout settingsView;
    Shader shader;
    Drawable shadowDrawable;
    private String source;
    private int statusBarHeight;
    int statusRow;
    int totalGradientHeight;
    float totalProgress;
    ArrayList<PremiumFeatureData> premiumFeatures = new ArrayList<>();
    Matrix matrix = new Matrix();
    Paint gradientPaint = new Paint(1);
    PremiumGradient.GradientTools gradientTools = new PremiumGradient.GradientTools(Theme.key_premiumGradientBackground1, Theme.key_premiumGradientBackground2, Theme.key_premiumGradientBackground3, Theme.key_premiumGradientBackground4);

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int severStringToFeatureType(String s) {
        char c;
        switch (s.hashCode()) {
            case -2145993328:
                if (s.equals("animated_userpics")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -1755514268:
                if (s.equals("voice_to_text")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1040323278:
                if (s.equals("no_ads")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1023650261:
                if (s.equals("more_upload")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -730864243:
                if (s.equals("profile_badge")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -448825858:
                if (s.equals("faster_download")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -165039170:
                if (s.equals("premium_stickers")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -96210874:
                if (s.equals("double_limits")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1182539900:
                if (s.equals("unique_reactions")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1219849581:
                if (s.equals("advanced_chat_management")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 1832801148:
                if (s.equals("app_icons")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 8;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 9;
            case '\b':
                return 6;
            case '\t':
                return 7;
            case '\n':
                return 10;
            default:
                return -1;
        }
    }

    public static String featureTypeToServerString(int type) {
        switch (type) {
            case 0:
                return "double_limits";
            case 1:
                return "more_upload";
            case 2:
                return "faster_download";
            case 3:
                return "no_ads";
            case 4:
                return "unique_reactions";
            case 5:
                return "premium_stickers";
            case 6:
                return "profile_badge";
            case 7:
                return "animated_userpics";
            case 8:
                return "voice_to_text";
            case 9:
                return "advanced_chat_management";
            case 10:
                return "app_icons";
            default:
                return null;
        }
    }

    public PremiumPreviewFragment setForcePremium() {
        this.forcePremium = true;
        return this;
    }

    public PremiumPreviewFragment(String source) {
        Bitmap createBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        this.gradientTextureBitmap = createBitmap;
        this.gradientCanvas = new Canvas(createBitmap);
        this.source = source;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.hasOwnBackground = true;
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 100.0f, new int[]{-816858, -2401123, -5806081, -11164161}, new float[]{0.0f, 0.32f, 0.5f, 1.0f}, Shader.TileMode.CLAMP);
        this.shader = linearGradient;
        linearGradient.setLocalMatrix(this.matrix);
        this.gradientPaint.setShader(this.shader);
        this.dummyCell = new PremiumFeatureCell(context);
        this.premiumFeatures.clear();
        fillPremiumFeaturesList(this.premiumFeatures, this.currentAccount);
        final Rect padding = new Rect();
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        this.shadowDrawable.getPadding(padding);
        if (Build.VERSION.SDK_INT >= 21) {
            this.statusBarHeight = AndroidUtilities.isTablet() ? 0 : AndroidUtilities.statusBarHeight;
        }
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.PremiumPreviewFragment.1
            boolean iconInterceptedTouch;
            int lastSize;

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent ev) {
                float iconX = PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageView.getX();
                float iconY = PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY();
                AndroidUtilities.rectTmp.set(iconX, iconY, PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth() + iconX, PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredHeight() + iconY);
                if (AndroidUtilities.rectTmp.contains(ev.getX(), ev.getY()) || this.iconInterceptedTouch) {
                    ev.offsetLocation(-iconX, -iconY);
                    if (ev.getAction() == 0 || ev.getAction() == 2) {
                        this.iconInterceptedTouch = true;
                    } else if (ev.getAction() == 1 || ev.getAction() == 3) {
                        this.iconInterceptedTouch = false;
                    }
                    PremiumPreviewFragment.this.backgroundView.imageView.dispatchTouchEvent(ev);
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int i = 0;
                if (View.MeasureSpec.getSize(widthMeasureSpec) > View.MeasureSpec.getSize(heightMeasureSpec)) {
                    PremiumPreviewFragment.this.isLandscapeMode = true;
                } else {
                    PremiumPreviewFragment.this.isLandscapeMode = false;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    PremiumPreviewFragment.this.statusBarHeight = AndroidUtilities.isTablet() ? 0 : AndroidUtilities.statusBarHeight;
                }
                PremiumPreviewFragment.this.backgroundView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, 0));
                PremiumPreviewFragment.this.particlesView.getLayoutParams().height = PremiumPreviewFragment.this.backgroundView.getMeasuredHeight();
                if (!PremiumPreviewFragment.this.getUserConfig().isPremium() && !PremiumPreviewFragment.this.forcePremium) {
                    i = AndroidUtilities.dp(68.0f);
                }
                int buttonHeight = i;
                PremiumPreviewFragment.this.layoutManager.setAdditionalHeight((PremiumPreviewFragment.this.statusBarHeight + buttonHeight) - AndroidUtilities.dp(16.0f));
                PremiumPreviewFragment.this.layoutManager.setMinimumLastViewHeight(buttonHeight);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                int size = (getMeasuredHeight() + getMeasuredWidth()) << 16;
                if (this.lastSize != size) {
                    PremiumPreviewFragment.this.updateBackgroundImage();
                }
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientScaleX = PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth() / getMeasuredWidth();
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientScaleY = PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredHeight() / getMeasuredHeight();
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartX = (PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageView.getX()) / getMeasuredWidth();
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartY = (PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY()) / getMeasuredHeight();
            }

            @Override // android.view.View
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                PremiumPreviewFragment.this.measureGradient(w, h);
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                PremiumPreviewFragment premiumPreviewFragment;
                int i = 0;
                if (!PremiumPreviewFragment.this.isDialogVisible) {
                    if (PremiumPreviewFragment.this.inc) {
                        PremiumPreviewFragment.this.progress += 0.016f;
                        if (PremiumPreviewFragment.this.progress > 3.0f) {
                            PremiumPreviewFragment.this.inc = false;
                        }
                    } else {
                        PremiumPreviewFragment.this.progress -= 0.016f;
                        if (PremiumPreviewFragment.this.progress < 1.0f) {
                            PremiumPreviewFragment.this.inc = true;
                        }
                    }
                }
                View firstView = null;
                if (PremiumPreviewFragment.this.listView.getLayoutManager() != null) {
                    firstView = PremiumPreviewFragment.this.listView.getLayoutManager().findViewByPosition(0);
                }
                PremiumPreviewFragment premiumPreviewFragment2 = PremiumPreviewFragment.this;
                if (firstView != null) {
                    i = firstView.getBottom();
                }
                premiumPreviewFragment2.currentYOffset = i;
                int h = PremiumPreviewFragment.this.actionBar.getBottom() + AndroidUtilities.dp(16.0f);
                PremiumPreviewFragment.this.totalProgress = 1.0f - ((premiumPreviewFragment.currentYOffset - h) / (PremiumPreviewFragment.this.firstViewHeight - h));
                PremiumPreviewFragment premiumPreviewFragment3 = PremiumPreviewFragment.this;
                premiumPreviewFragment3.totalProgress = Utilities.clamp(premiumPreviewFragment3.totalProgress, 1.0f, 0.0f);
                int maxTop = PremiumPreviewFragment.this.actionBar.getBottom() + AndroidUtilities.dp(16.0f);
                if (PremiumPreviewFragment.this.currentYOffset < maxTop) {
                    PremiumPreviewFragment.this.currentYOffset = maxTop;
                }
                float oldProgress = PremiumPreviewFragment.this.progressToFull;
                PremiumPreviewFragment.this.progressToFull = 0.0f;
                if (PremiumPreviewFragment.this.currentYOffset < AndroidUtilities.dp(30.0f) + maxTop) {
                    PremiumPreviewFragment.this.progressToFull = ((AndroidUtilities.dp(30.0f) + maxTop) - PremiumPreviewFragment.this.currentYOffset) / AndroidUtilities.dp(30.0f);
                }
                if (PremiumPreviewFragment.this.isLandscapeMode) {
                    PremiumPreviewFragment.this.progressToFull = 1.0f;
                    PremiumPreviewFragment.this.totalProgress = 1.0f;
                }
                if (oldProgress != PremiumPreviewFragment.this.progressToFull) {
                    PremiumPreviewFragment.this.listView.invalidate();
                }
                float fromTranslation = (PremiumPreviewFragment.this.currentYOffset - ((PremiumPreviewFragment.this.actionBar.getMeasuredHeight() + PremiumPreviewFragment.this.backgroundView.getMeasuredHeight()) - PremiumPreviewFragment.this.statusBarHeight)) + AndroidUtilities.dp(16.0f);
                float toTranslation = (((((PremiumPreviewFragment.this.actionBar.getMeasuredHeight() - PremiumPreviewFragment.this.statusBarHeight) - PremiumPreviewFragment.this.backgroundView.titleView.getMeasuredHeight()) / 2.0f) + PremiumPreviewFragment.this.statusBarHeight) - PremiumPreviewFragment.this.backgroundView.getTop()) - PremiumPreviewFragment.this.backgroundView.titleView.getTop();
                float translationsY = Math.max(toTranslation, fromTranslation);
                float iconTranslationsY = ((-translationsY) / 4.0f) + AndroidUtilities.dp(16.0f);
                PremiumPreviewFragment.this.backgroundView.setTranslationY(translationsY);
                PremiumPreviewFragment.this.backgroundView.imageView.setTranslationY(AndroidUtilities.dp(16.0f) + iconTranslationsY);
                float s = ((1.0f - PremiumPreviewFragment.this.totalProgress) * 0.4f) + 0.6f;
                float alpha = 1.0f - (PremiumPreviewFragment.this.totalProgress > 0.5f ? (PremiumPreviewFragment.this.totalProgress - 0.5f) / 0.5f : 0.0f);
                PremiumPreviewFragment.this.backgroundView.imageView.setScaleX(s);
                PremiumPreviewFragment.this.backgroundView.imageView.setScaleY(s);
                PremiumPreviewFragment.this.backgroundView.imageView.setAlpha(alpha);
                PremiumPreviewFragment.this.backgroundView.subtitleView.setAlpha(alpha);
                PremiumPreviewFragment.this.particlesView.setAlpha(1.0f - PremiumPreviewFragment.this.totalProgress);
                PremiumPreviewFragment.this.particlesView.setTranslationY(((-(PremiumPreviewFragment.this.particlesView.getMeasuredHeight() - PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth())) / 2.0f) + PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY());
                float toX = AndroidUtilities.dp(72.0f) - PremiumPreviewFragment.this.backgroundView.titleView.getLeft();
                float f = PremiumPreviewFragment.this.totalProgress > 0.3f ? (PremiumPreviewFragment.this.totalProgress - 0.3f) / 0.7f : 0.0f;
                PremiumPreviewFragment.this.backgroundView.titleView.setTranslationX((1.0f - CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(1.0f - f)) * toX);
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartX = ((PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageView.getX()) + ((getMeasuredWidth() * 0.1f) * PremiumPreviewFragment.this.progress)) / getMeasuredWidth();
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartY = (PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY()) / getMeasuredHeight();
                if (!PremiumPreviewFragment.this.isDialogVisible) {
                    invalidate();
                }
                PremiumPreviewFragment.this.gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), getMeasuredHeight(), (-getMeasuredWidth()) * 0.1f * PremiumPreviewFragment.this.progress, 0.0f);
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), PremiumPreviewFragment.this.currentYOffset + AndroidUtilities.dp(20.0f), PremiumPreviewFragment.this.gradientTools.paint);
                super.dispatchDraw(canvas);
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child == PremiumPreviewFragment.this.listView) {
                    canvas.save();
                    canvas.clipRect(0, PremiumPreviewFragment.this.actionBar.getBottom(), getMeasuredWidth(), getMeasuredHeight());
                    super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return true;
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        this.contentView = frameLayout;
        frameLayout.setFitsSystemWindows(true);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.PremiumPreviewFragment.2
            @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onDraw(Canvas canvas) {
                PremiumPreviewFragment.this.shadowDrawable.setBounds((int) ((-padding.left) - (AndroidUtilities.dp(16.0f) * PremiumPreviewFragment.this.progressToFull)), (PremiumPreviewFragment.this.currentYOffset - padding.top) - AndroidUtilities.dp(16.0f), (int) (getMeasuredWidth() + padding.right + (AndroidUtilities.dp(16.0f) * PremiumPreviewFragment.this.progressToFull)), getMeasuredHeight());
                PremiumPreviewFragment.this.shadowDrawable.draw(canvas);
                super.onDraw(canvas);
            }
        };
        this.listView = recyclerListView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(context, (AndroidUtilities.dp(68.0f) + this.statusBarHeight) - AndroidUtilities.dp(16.0f), this.listView);
        this.layoutManager = fillLastLinearLayoutManager;
        recyclerListView.setLayoutManager(fillLastLinearLayoutManager);
        this.layoutManager.setFixedLastItemHeight();
        this.listView.setAdapter(new Adapter());
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.PremiumPreviewFragment.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    int maxTop = PremiumPreviewFragment.this.actionBar.getBottom() + AndroidUtilities.dp(16.0f);
                    if (PremiumPreviewFragment.this.totalProgress > 0.5f) {
                        PremiumPreviewFragment.this.listView.smoothScrollBy(0, PremiumPreviewFragment.this.currentYOffset - maxTop);
                    } else {
                        View firstView = null;
                        if (PremiumPreviewFragment.this.listView.getLayoutManager() != null) {
                            firstView = PremiumPreviewFragment.this.listView.getLayoutManager().findViewByPosition(0);
                        }
                        if (firstView != null && firstView.getTop() < 0) {
                            PremiumPreviewFragment.this.listView.smoothScrollBy(0, firstView.getTop());
                        }
                    }
                }
                PremiumPreviewFragment.this.checkButtonDivider();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                PremiumPreviewFragment.this.contentView.invalidate();
                PremiumPreviewFragment.this.checkButtonDivider();
            }
        });
        this.backgroundView = new BackgroundView(context) { // from class: org.telegram.ui.PremiumPreviewFragment.4
            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return true;
            }
        };
        this.particlesView = new StarParticlesView(context);
        this.backgroundView.imageView.setStarParticlesView(this.particlesView);
        this.contentView.addView(this.particlesView, LayoutHelper.createFrame(-1, -2.0f));
        this.contentView.addView(this.backgroundView, LayoutHelper.createFrame(-1, -2.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.PremiumPreviewFragment$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                PremiumPreviewFragment.this.m4317lambda$createView$0$orgtelegramuiPremiumPreviewFragment(view, i);
            }
        });
        this.contentView.addView(this.listView);
        PremiumButtonView premiumButtonView = new PremiumButtonView(context, false);
        this.premiumButtonView = premiumButtonView;
        premiumButtonView.setButton(getPremiumButtonText(this.currentAccount), new View.OnClickListener() { // from class: org.telegram.ui.PremiumPreviewFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PremiumPreviewFragment.this.m4318lambda$createView$1$orgtelegramuiPremiumPreviewFragment(view);
            }
        });
        this.buttonContainer = new FrameLayout(context);
        View view = new View(context);
        this.buttonDivider = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_divider));
        this.buttonContainer.addView(this.buttonDivider, LayoutHelper.createFrame(-1, 1.0f));
        this.buttonDivider.getLayoutParams().height = 1;
        AndroidUtilities.updateViewVisibilityAnimated(this.buttonDivider, true, 1.0f, false);
        this.buttonContainer.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
        this.buttonContainer.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        this.contentView.addView(this.buttonContainer, LayoutHelper.createFrame(-1, 68, 80));
        this.fragmentView = this.contentView;
        this.actionBar.setBackground(null);
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.PremiumPreviewFragment.5
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    PremiumPreviewFragment.this.finishFragment();
                }
            }
        });
        this.actionBar.setForceSkipTouches(true);
        updateColors();
        updateRows();
        this.backgroundView.imageView.startEnterAnimation(-180, 200L);
        if (this.forcePremium) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PremiumPreviewFragment$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    PremiumPreviewFragment.this.m4319lambda$createView$2$orgtelegramuiPremiumPreviewFragment();
                }
            }, 400L);
        }
        MediaDataController.getInstance(this.currentAccount).preloadPremiumPreviewStickers();
        sentShowScreenStat(this.source);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PremiumPreviewFragment */
    public /* synthetic */ void m4317lambda$createView$0$orgtelegramuiPremiumPreviewFragment(View view, int position) {
        if (view instanceof PremiumFeatureCell) {
            PremiumFeatureCell cell = (PremiumFeatureCell) view;
            sentShowFeaturePreview(this.currentAccount, cell.data.type);
            if (cell.data.type == 0) {
                DoubledLimitsBottomSheet bottomSheet = new DoubledLimitsBottomSheet(this, this.currentAccount);
                bottomSheet.setParentFragment(this);
                showDialog(bottomSheet);
                return;
            }
            showDialog(new PremiumFeatureBottomSheet(this, cell.data.type, false));
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PremiumPreviewFragment */
    public /* synthetic */ void m4318lambda$createView$1$orgtelegramuiPremiumPreviewFragment(View v) {
        buyPremium(this);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PremiumPreviewFragment */
    public /* synthetic */ void m4319lambda$createView$2$orgtelegramuiPremiumPreviewFragment() {
        getMediaDataController().loadPremiumPromo(false);
    }

    public static void buyPremium(BaseFragment fragment) {
        buyPremium(fragment, "settings");
    }

    public static void fillPremiumFeaturesList(ArrayList<PremiumFeatureData> premiumFeatures, int currentAccount) {
        final MessagesController messagesController = MessagesController.getInstance(currentAccount);
        premiumFeatures.add(new PremiumFeatureData(0, R.drawable.msg_premium_limits, LocaleController.getString("PremiumPreviewLimits", R.string.PremiumPreviewLimits), LocaleController.formatString("PremiumPreviewLimitsDescription", R.string.PremiumPreviewLimitsDescription, Integer.valueOf(messagesController.channelsLimitPremium), Integer.valueOf(messagesController.dialogFiltersLimitPremium), Integer.valueOf(messagesController.dialogFiltersPinnedLimitPremium), Integer.valueOf(messagesController.publicLinksLimitPremium), 4)));
        premiumFeatures.add(new PremiumFeatureData(1, R.drawable.msg_premium_uploads, LocaleController.getString("PremiumPreviewUploads", R.string.PremiumPreviewUploads), LocaleController.getString("PremiumPreviewUploadsDescription", R.string.PremiumPreviewUploadsDescription)));
        premiumFeatures.add(new PremiumFeatureData(2, R.drawable.msg_premium_speed, LocaleController.getString("PremiumPreviewDownloadSpeed", R.string.PremiumPreviewDownloadSpeed), LocaleController.getString("PremiumPreviewDownloadSpeedDescription", R.string.PremiumPreviewDownloadSpeedDescription)));
        premiumFeatures.add(new PremiumFeatureData(8, R.drawable.msg_premium_voice, LocaleController.getString("PremiumPreviewVoiceToText", R.string.PremiumPreviewVoiceToText), LocaleController.getString("PremiumPreviewVoiceToTextDescription", R.string.PremiumPreviewVoiceToTextDescription)));
        premiumFeatures.add(new PremiumFeatureData(3, R.drawable.msg_premium_ads, LocaleController.getString("PremiumPreviewNoAds", R.string.PremiumPreviewNoAds), LocaleController.getString("PremiumPreviewNoAdsDescription", R.string.PremiumPreviewNoAdsDescription)));
        premiumFeatures.add(new PremiumFeatureData(4, R.drawable.msg_premium_reactions, LocaleController.getString("PremiumPreviewReactions", R.string.PremiumPreviewReactions), LocaleController.getString("PremiumPreviewReactionsDescription", R.string.PremiumPreviewReactionsDescription)));
        premiumFeatures.add(new PremiumFeatureData(5, R.drawable.msg_premium_stickers, LocaleController.getString("PremiumPreviewStickers", R.string.PremiumPreviewStickers), LocaleController.getString("PremiumPreviewStickersDescription", R.string.PremiumPreviewStickersDescription)));
        premiumFeatures.add(new PremiumFeatureData(9, R.drawable.msg_premium_tools, LocaleController.getString("PremiumPreviewAdvancedChatManagement", R.string.PremiumPreviewAdvancedChatManagement), LocaleController.getString("PremiumPreviewAdvancedChatManagementDescription", R.string.PremiumPreviewAdvancedChatManagementDescription)));
        premiumFeatures.add(new PremiumFeatureData(6, R.drawable.msg_premium_badge, LocaleController.getString("PremiumPreviewProfileBadge", R.string.PremiumPreviewProfileBadge), LocaleController.getString("PremiumPreviewProfileBadgeDescription", R.string.PremiumPreviewProfileBadgeDescription)));
        premiumFeatures.add(new PremiumFeatureData(7, R.drawable.msg_premium_avatar, LocaleController.getString("PremiumPreviewAnimatedProfiles", R.string.PremiumPreviewAnimatedProfiles), LocaleController.getString("PremiumPreviewAnimatedProfilesDescription", R.string.PremiumPreviewAnimatedProfilesDescription)));
        premiumFeatures.add(new PremiumFeatureData(10, R.drawable.msg_premium_icons, LocaleController.getString("PremiumPreviewAppIcon", R.string.PremiumPreviewAppIcon), LocaleController.getString("PremiumPreviewAppIconDescription", R.string.PremiumPreviewAppIconDescription)));
        if (messagesController.premiumFeaturesTypesToPosition.size() > 0) {
            int i = 0;
            while (i < premiumFeatures.size()) {
                if (messagesController.premiumFeaturesTypesToPosition.get(premiumFeatures.get(i).type, -1) == -1) {
                    premiumFeatures.remove(i);
                    i--;
                }
                i++;
            }
        }
        Collections.sort(premiumFeatures, new Comparator() { // from class: org.telegram.ui.PremiumPreviewFragment$$ExternalSyntheticLambda6
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return PremiumPreviewFragment.lambda$fillPremiumFeaturesList$3(MessagesController.this, (PremiumPreviewFragment.PremiumFeatureData) obj, (PremiumPreviewFragment.PremiumFeatureData) obj2);
            }
        });
    }

    public static /* synthetic */ int lambda$fillPremiumFeaturesList$3(MessagesController messagesController, PremiumFeatureData o1, PremiumFeatureData o2) {
        int type1 = messagesController.premiumFeaturesTypesToPosition.get(o1.type, Integer.MAX_VALUE);
        int type2 = messagesController.premiumFeaturesTypesToPosition.get(o2.type, Integer.MAX_VALUE);
        return type1 - type2;
    }

    public void updateBackgroundImage() {
        if (this.contentView.getMeasuredWidth() == 0 || this.contentView.getMeasuredHeight() == 0) {
            return;
        }
        this.gradientTools.gradientMatrix(0, 0, this.contentView.getMeasuredWidth(), this.contentView.getMeasuredHeight(), 0.0f, 0.0f);
        this.gradientCanvas.save();
        this.gradientCanvas.scale(100.0f / this.contentView.getMeasuredWidth(), 100.0f / this.contentView.getMeasuredHeight());
        this.gradientCanvas.drawRect(0.0f, 0.0f, this.contentView.getMeasuredWidth(), this.contentView.getMeasuredHeight(), this.gradientTools.paint);
        this.gradientCanvas.restore();
        this.backgroundView.imageView.setBackgroundBitmap(this.gradientTextureBitmap);
    }

    public void checkButtonDivider() {
        AndroidUtilities.updateViewVisibilityAnimated(this.buttonDivider, this.listView.canScrollVertically(1), 1.0f, true);
    }

    public static void buyPremium(final BaseFragment fragment, String source) {
        if (BuildVars.IS_BILLING_UNAVAILABLE) {
            fragment.showDialog(new PremiumNotAvailableBottomSheet(fragment));
            return;
        }
        sentPremiumButtonClick();
        if (BuildVars.useInvoiceBilling()) {
            Activity activity = fragment.getParentActivity();
            if (activity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) activity;
                if (!TextUtils.isEmpty(fragment.getMessagesController().premiumBotUsername)) {
                    launchActivity.setNavigateToPremiumBot(true);
                    launchActivity.onNewIntent(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/" + fragment.getMessagesController().premiumBotUsername + "?start=" + source)));
                } else if (!TextUtils.isEmpty(fragment.getMessagesController().premiumInvoiceSlug)) {
                    launchActivity.onNewIntent(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/$" + fragment.getMessagesController().premiumInvoiceSlug)));
                }
            }
        } else if (BillingController.PREMIUM_PRODUCT_DETAILS == null) {
        } else {
            final List<ProductDetails.SubscriptionOfferDetails> offerDetails = BillingController.PREMIUM_PRODUCT_DETAILS.getSubscriptionOfferDetails();
            if (offerDetails.isEmpty()) {
                return;
            }
            BillingController.getInstance().addResultListener(BillingController.PREMIUM_PRODUCT_ID, new Consumer() { // from class: org.telegram.ui.PremiumPreviewFragment$$ExternalSyntheticLambda3
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    PremiumPreviewFragment.lambda$buyPremium$4(BaseFragment.this, (BillingResult) obj);
                }
            });
            final TLRPC.TL_payments_canPurchasePremium req = new TLRPC.TL_payments_canPurchasePremium();
            fragment.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PremiumPreviewFragment$$ExternalSyntheticLambda7
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PremiumPreviewFragment$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            PremiumPreviewFragment.lambda$buyPremium$5(TLObject.this, r2, r3, tL_error, r5);
                        }
                    });
                }
            });
        }
    }

    public static /* synthetic */ void lambda$buyPremium$4(BaseFragment fragment, BillingResult billingResult) {
        if (billingResult.getResponseCode() == 0) {
            if (fragment instanceof PremiumPreviewFragment) {
                PremiumPreviewFragment premiumPreviewFragment = (PremiumPreviewFragment) fragment;
                premiumPreviewFragment.setForcePremium();
                premiumPreviewFragment.getMediaDataController().loadPremiumPromo(false);
                premiumPreviewFragment.listView.smoothScrollToPosition(0);
            } else {
                fragment.presentFragment(new PremiumPreviewFragment(null).setForcePremium());
            }
            if (fragment.getParentActivity() instanceof LaunchActivity) {
                try {
                    fragment.getFragmentView().performHapticFeedback(3, 2);
                } catch (Exception e) {
                }
                ((LaunchActivity) fragment.getParentActivity()).getFireworksOverlay().start();
            }
        } else if (billingResult.getResponseCode() == 1) {
            sentPremiumBuyCanceled();
        }
    }

    public static /* synthetic */ void lambda$buyPremium$5(TLObject response, BaseFragment fragment, List offerDetails, TLRPC.TL_error error, TLRPC.TL_payments_canPurchasePremium req) {
        if (response instanceof TLRPC.TL_boolTrue) {
            BillingController.getInstance().launchBillingFlow(fragment.getParentActivity(), Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(BillingController.PREMIUM_PRODUCT_DETAILS).setOfferToken(((ProductDetails.SubscriptionOfferDetails) offerDetails.get(0)).getOfferToken()).build()));
        } else {
            AlertsCreator.processError(fragment.getCurrentAccount(), error, fragment, req, new Object[0]);
        }
    }

    public static String getPremiumButtonText(int currentAccount) {
        Currency currency;
        if (BuildVars.IS_BILLING_UNAVAILABLE) {
            return LocaleController.getString((int) R.string.SubscribeToPremiumNotAvailable);
        }
        if (BuildVars.useInvoiceBilling()) {
            TLRPC.TL_help_premiumPromo premiumPromo = MediaDataController.getInstance(currentAccount).getPremiumPromo();
            if (premiumPromo != null && (currency = Currency.getInstance(premiumPromo.currency)) != null) {
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
                numberFormat.setCurrency(currency);
                double d = premiumPromo.monthly_amount;
                double pow = Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(premiumPromo.currency));
                Double.isNaN(d);
                return LocaleController.formatString(R.string.SubscribeToPremium, numberFormat.format(d / pow));
            }
            return LocaleController.getString((int) R.string.SubscribeToPremiumNoPrice);
        }
        String price = null;
        if (BillingController.PREMIUM_PRODUCT_DETAILS != null) {
            List<ProductDetails.SubscriptionOfferDetails> details = BillingController.PREMIUM_PRODUCT_DETAILS.getSubscriptionOfferDetails();
            if (!details.isEmpty()) {
                ProductDetails.SubscriptionOfferDetails offerDetails = details.get(0);
                Iterator<ProductDetails.PricingPhase> it = offerDetails.getPricingPhases().getPricingPhaseList().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    ProductDetails.PricingPhase phase = it.next();
                    if (phase.getBillingPeriod().equals("P1M")) {
                        price = phase.getFormattedPrice();
                        break;
                    }
                }
            }
        }
        return price == null ? LocaleController.getString((int) R.string.Loading) : LocaleController.formatString(R.string.SubscribeToPremium, price);
    }

    public void measureGradient(int w, int h) {
        int yOffset = 0;
        for (int i = 0; i < this.premiumFeatures.size(); i++) {
            this.dummyCell.setData(this.premiumFeatures.get(i), false);
            this.dummyCell.measure(View.MeasureSpec.makeMeasureSpec(w, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(h, Integer.MIN_VALUE));
            this.premiumFeatures.get(i).yOffset = yOffset;
            yOffset += this.dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = yOffset;
    }

    private void updateRows() {
        int buttonHeight = 0;
        this.rowCount = 0;
        this.sectionRow = -1;
        this.statusRow = -1;
        this.privacyRow = -1;
        int i = 0 + 1;
        this.rowCount = i;
        this.paddingRow = 0;
        this.featuresStartRow = i;
        int size = i + this.premiumFeatures.size();
        this.rowCount = size;
        this.featuresEndRow = size;
        int i2 = size + 1;
        this.rowCount = i2;
        this.statusRow = size;
        this.rowCount = i2 + 1;
        this.lastPaddingRow = i2;
        if (!getUserConfig().isPremium() && !this.forcePremium) {
            this.buttonContainer.setVisibility(0);
        } else {
            this.buttonContainer.setVisibility(8);
        }
        if (this.buttonContainer.getVisibility() == 0) {
            buttonHeight = AndroidUtilities.dp(64.0f);
        }
        this.layoutManager.setAdditionalHeight((this.statusBarHeight + buttonHeight) - AndroidUtilities.dp(16.0f));
        this.layoutManager.setMinimumLastViewHeight(buttonHeight);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent event) {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        if (getMessagesController().premiumLocked) {
            return false;
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.premiumPromoUpdated);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.premiumPromoUpdated);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.billingProductDetailsUpdated || id == NotificationCenter.premiumPromoUpdated) {
            this.premiumButtonView.buttonTextView.setText(getPremiumButtonText(this.currentAccount));
        }
        if (id == NotificationCenter.currentUserPremiumStatusChanged || id == NotificationCenter.premiumPromoUpdated) {
            this.backgroundView.updateText();
            updateRows();
            this.listView.getAdapter().notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class Adapter extends RecyclerListView.SelectionAdapter {
        private static final int TYPE_BOTTOM_PADDING = 6;
        private static final int TYPE_BUTTON = 3;
        private static final int TYPE_FEATURE = 1;
        private static final int TYPE_HELP_US = 4;
        private static final int TYPE_PADDING = 0;
        private static final int TYPE_SHADOW_SECTION = 2;
        private static final int TYPE_STATUS_TEXT = 5;

        private Adapter() {
            PremiumPreviewFragment.this = r1;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            switch (viewType) {
                case 1:
                    view = new PremiumFeatureCell(context) { // from class: org.telegram.ui.PremiumPreviewFragment.Adapter.2
                        @Override // org.telegram.ui.PremiumFeatureCell, android.view.ViewGroup, android.view.View
                        public void dispatchDraw(Canvas canvas) {
                            AndroidUtilities.rectTmp.set(this.imageView.getLeft(), this.imageView.getTop(), this.imageView.getRight(), this.imageView.getBottom());
                            PremiumPreviewFragment.this.matrix.reset();
                            PremiumPreviewFragment.this.matrix.postScale(1.0f, PremiumPreviewFragment.this.totalGradientHeight / 100.0f, 0.0f, 0.0f);
                            PremiumPreviewFragment.this.matrix.postTranslate(0.0f, -this.data.yOffset);
                            PremiumPreviewFragment.this.shader.setLocalMatrix(PremiumPreviewFragment.this.matrix);
                            canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), PremiumPreviewFragment.this.gradientPaint);
                            super.dispatchDraw(canvas);
                        }
                    };
                    break;
                case 2:
                    ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_windowBackgroundGray));
                    Drawable shadowDrawable = Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.getColor(Theme.key_windowBackgroundGrayShadow));
                    Drawable background = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(background, shadowDrawable, 0, 0);
                    combinedDrawable.setFullsize(true);
                    shadowSectionCell.setBackgroundDrawable(combinedDrawable);
                    view = shadowSectionCell;
                    break;
                case 3:
                default:
                    view = new View(context) { // from class: org.telegram.ui.PremiumPreviewFragment.Adapter.1
                        @Override // android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            if (!PremiumPreviewFragment.this.isLandscapeMode) {
                                int h = AndroidUtilities.dp(300.0f) + PremiumPreviewFragment.this.statusBarHeight;
                                if (PremiumPreviewFragment.this.backgroundView.getMeasuredHeight() + AndroidUtilities.dp(24.0f) > h) {
                                    h = PremiumPreviewFragment.this.backgroundView.getMeasuredHeight() + AndroidUtilities.dp(24.0f);
                                }
                                PremiumPreviewFragment.this.firstViewHeight = h;
                            } else {
                                PremiumPreviewFragment.this.firstViewHeight = (PremiumPreviewFragment.this.statusBarHeight + PremiumPreviewFragment.this.actionBar.getMeasuredHeight()) - AndroidUtilities.dp(16.0f);
                            }
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(PremiumPreviewFragment.this.firstViewHeight, C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    break;
                case 4:
                    view = new AboutPremiumView(context);
                    break;
                case 5:
                    view = new TextInfoPrivacyCell(context);
                    break;
                case 6:
                    View view2 = new View(context);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    view = view2;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.TL_help_premiumPromo premiumPromo;
            Drawable background;
            Drawable shadowDrawable;
            int i;
            TextStyleSpan.TextStyleRun run;
            TextStyleSpan[] textStyleSpanArr;
            int i2;
            boolean z = true;
            int i3 = 0;
            if (position >= PremiumPreviewFragment.this.featuresStartRow && position < PremiumPreviewFragment.this.featuresEndRow) {
                PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) holder.itemView;
                PremiumFeatureData premiumFeatureData = PremiumPreviewFragment.this.premiumFeatures.get(position - PremiumPreviewFragment.this.featuresStartRow);
                if (position == PremiumPreviewFragment.this.featuresEndRow - 1) {
                    z = false;
                }
                premiumFeatureCell.setData(premiumFeatureData, z);
            } else if (position == PremiumPreviewFragment.this.statusRow || position == PremiumPreviewFragment.this.privacyRow) {
                TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                Drawable shadowDrawable2 = Theme.getThemedDrawable(privacyCell.getContext(), (int) R.drawable.greydivider, Theme.getColor(Theme.key_windowBackgroundGrayShadow));
                Drawable background2 = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray));
                CombinedDrawable combinedDrawable = new CombinedDrawable(background2, shadowDrawable2, 0, 0);
                combinedDrawable.setFullsize(true);
                privacyCell.setBackground(combinedDrawable);
                if (position != PremiumPreviewFragment.this.statusRow || (premiumPromo = PremiumPreviewFragment.this.getMediaDataController().getPremiumPromo()) == null) {
                    return;
                }
                SpannableString spannableString = new SpannableString(premiumPromo.status_text);
                MediaDataController.addTextStyleRuns(premiumPromo.status_entities, premiumPromo.status_text, spannableString);
                TextStyleSpan[] textStyleSpanArr2 = (TextStyleSpan[]) spannableString.getSpans(0, spannableString.length(), TextStyleSpan.class);
                int length = textStyleSpanArr2.length;
                while (i3 < length) {
                    TextStyleSpan span = textStyleSpanArr2[i3];
                    TextStyleSpan.TextStyleRun run2 = span.getTextStyleRun();
                    boolean setRun = false;
                    String url = run2.urlEntity != null ? TextUtils.substring(premiumPromo.status_text, run2.urlEntity.offset, run2.urlEntity.offset + run2.urlEntity.length) : null;
                    if (run2.urlEntity instanceof TLRPC.TL_messageEntityBotCommand) {
                        spannableString.setSpan(new URLSpanBotCommand(url, 0, run2), run2.start, run2.end, 33);
                        shadowDrawable = shadowDrawable2;
                        background = background2;
                        run = run2;
                        i = length;
                        textStyleSpanArr = textStyleSpanArr2;
                    } else {
                        if ((run2.urlEntity instanceof TLRPC.TL_messageEntityHashtag) || (run2.urlEntity instanceof TLRPC.TL_messageEntityMention)) {
                            shadowDrawable = shadowDrawable2;
                            background = background2;
                            run = run2;
                            i = length;
                            textStyleSpanArr = textStyleSpanArr2;
                            i2 = 33;
                        } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityCashtag) {
                            shadowDrawable = shadowDrawable2;
                            background = background2;
                            run = run2;
                            i = length;
                            textStyleSpanArr = textStyleSpanArr2;
                            i2 = 33;
                        } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityEmail) {
                            spannableString.setSpan(new URLSpanReplacement(MailTo.MAILTO_SCHEME + url, run2), run2.start, run2.end, 33);
                            shadowDrawable = shadowDrawable2;
                            background = background2;
                            run = run2;
                            i = length;
                            textStyleSpanArr = textStyleSpanArr2;
                        } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityUrl) {
                            String lowerCase = url.toLowerCase();
                            if (!lowerCase.contains("://")) {
                                spannableString.setSpan(new URLSpanBrowser("http://" + url, run2), run2.start, run2.end, 33);
                            } else {
                                spannableString.setSpan(new URLSpanBrowser(url, run2), run2.start, run2.end, 33);
                            }
                            shadowDrawable = shadowDrawable2;
                            background = background2;
                            run = run2;
                            i = length;
                            textStyleSpanArr = textStyleSpanArr2;
                        } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityBankCard) {
                            spannableString.setSpan(new URLSpanNoUnderline("card:" + url, run2), run2.start, run2.end, 33);
                            shadowDrawable = shadowDrawable2;
                            background = background2;
                            run = run2;
                            i = length;
                            textStyleSpanArr = textStyleSpanArr2;
                        } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityPhone) {
                            String tel = PhoneFormat.stripExceptNumbers(url);
                            if (url.startsWith("+")) {
                                tel = "+" + tel;
                            }
                            spannableString.setSpan(new URLSpanBrowser("tel:" + tel, run2), run2.start, run2.end, 33);
                            shadowDrawable = shadowDrawable2;
                            background = background2;
                            run = run2;
                            i = length;
                            textStyleSpanArr = textStyleSpanArr2;
                        } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityTextUrl) {
                            URLSpanReplacement spanReplacement = new URLSpanReplacement(run2.urlEntity.url, run2);
                            spanReplacement.setNavigateToPremiumBot(true);
                            spannableString.setSpan(spanReplacement, run2.start, run2.end, 33);
                            shadowDrawable = shadowDrawable2;
                            background = background2;
                            run = run2;
                            i = length;
                            textStyleSpanArr = textStyleSpanArr2;
                        } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityMentionName) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("");
                            shadowDrawable = shadowDrawable2;
                            background = background2;
                            sb.append(((TLRPC.TL_messageEntityMentionName) run2.urlEntity).user_id);
                            spannableString.setSpan(new URLSpanUserMention(sb.toString(), 0, run2), run2.start, run2.end, 33);
                            run = run2;
                            i = length;
                            textStyleSpanArr = textStyleSpanArr2;
                        } else {
                            shadowDrawable = shadowDrawable2;
                            background = background2;
                            if (run2.urlEntity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                                spannableString.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_inputMessageEntityMentionName) run2.urlEntity).user_id.user_id, 0, run2), run2.start, run2.end, 33);
                                run = run2;
                                i = length;
                                textStyleSpanArr = textStyleSpanArr2;
                            } else if ((run2.flags & 4) != 0) {
                                run = run2;
                                i = length;
                                textStyleSpanArr = textStyleSpanArr2;
                                spannableString.setSpan(new URLSpanMono(spannableString, run2.start, run2.end, (byte) 0, run), run.start, run.end, 33);
                            } else {
                                run = run2;
                                i = length;
                                textStyleSpanArr = textStyleSpanArr2;
                                setRun = true;
                                spannableString.setSpan(new TextStyleSpan(run), run.start, run.end, 33);
                            }
                        }
                        spannableString.setSpan(new URLSpanNoUnderline(url, run), run.start, run.end, i2);
                    }
                    if (!setRun && (run.flags & 256) != 0) {
                        spannableString.setSpan(new TextStyleSpan(run), run.start, run.end, 33);
                    }
                    i3++;
                    textStyleSpanArr2 = textStyleSpanArr;
                    length = i;
                    shadowDrawable2 = shadowDrawable;
                    background2 = background;
                }
                privacyCell.setText(spannableString);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return PremiumPreviewFragment.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == PremiumPreviewFragment.this.paddingRow) {
                return 0;
            }
            if (position >= PremiumPreviewFragment.this.featuresStartRow && position < PremiumPreviewFragment.this.featuresEndRow) {
                return 1;
            }
            if (position == PremiumPreviewFragment.this.sectionRow) {
                return 2;
            }
            if (position == PremiumPreviewFragment.this.helpUsRow) {
                return 4;
            }
            if (position == PremiumPreviewFragment.this.statusRow || position == PremiumPreviewFragment.this.privacyRow) {
                return 5;
            }
            return position == PremiumPreviewFragment.this.lastPaddingRow ? 6 : 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 1;
        }
    }

    /* loaded from: classes4.dex */
    public static class PremiumFeatureData {
        public final String description;
        public final int icon;
        public final String title;
        public final int type;
        public int yOffset;

        public PremiumFeatureData(int type, int icon, String title, String description) {
            this.type = type;
            this.icon = icon;
            this.title = title;
            this.description = description;
        }
    }

    /* loaded from: classes4.dex */
    public class BackgroundView extends LinearLayout {
        private final GLIconTextureView imageView;
        private final TextView subtitleView;
        TextView titleView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BackgroundView(final Context context) {
            super(context);
            PremiumPreviewFragment.this = r12;
            setOrientation(1);
            GLIconTextureView gLIconTextureView = new GLIconTextureView(context, 0) { // from class: org.telegram.ui.PremiumPreviewFragment.BackgroundView.1
                @Override // org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView
                public void onLongPress() {
                    super.onLongPress();
                    if (PremiumPreviewFragment.this.settingsView != null && !BuildVars.DEBUG_PRIVATE_VERSION) {
                        return;
                    }
                    PremiumPreviewFragment.this.settingsView = new FrameLayout(context);
                    ScrollView scrollView = new ScrollView(context);
                    LinearLayout linearLayout = new GLIconSettingsView(context, BackgroundView.this.imageView.mRenderer);
                    scrollView.addView(linearLayout);
                    PremiumPreviewFragment.this.settingsView.addView(scrollView);
                    PremiumPreviewFragment.this.settingsView.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
                    PremiumPreviewFragment.this.contentView.addView(PremiumPreviewFragment.this.settingsView, LayoutHelper.createFrame(-1, -1, 80));
                    ((ViewGroup.MarginLayoutParams) PremiumPreviewFragment.this.settingsView.getLayoutParams()).topMargin = PremiumPreviewFragment.this.currentYOffset;
                    PremiumPreviewFragment.this.settingsView.setTranslationY(AndroidUtilities.dp(1000.0f));
                    PremiumPreviewFragment.this.settingsView.animate().translationY(1.0f).setDuration(300L);
                }
            };
            this.imageView = gLIconTextureView;
            addView(gLIconTextureView, LayoutHelper.createLinear(190, 190, 1));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 22.0f);
            this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titleView.setGravity(1);
            addView(this.titleView, LayoutHelper.createLinear(-2, -2, 0.0f, 1, 16, 20, 16, 0));
            TextView textView2 = new TextView(context);
            this.subtitleView = textView2;
            textView2.setTextSize(1, 14.0f);
            textView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            textView2.setGravity(1);
            addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 7, 16, 0));
            updateText();
        }

        public void updateText() {
            this.titleView.setText(LocaleController.getString(PremiumPreviewFragment.this.forcePremium ? R.string.TelegramPremiumSubscribedTitle : R.string.TelegramPremium));
            this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.getString((PremiumPreviewFragment.this.getUserConfig().isPremium() || PremiumPreviewFragment.this.forcePremium) ? R.string.TelegramPremiumSubscribedSubtitle : R.string.TelegramPremiumSubtitle)));
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        this.backgroundView.imageView.setPaused(false);
        this.backgroundView.imageView.setDialogVisible(false);
        this.particlesView.setPaused(false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        this.backgroundView.imageView.setDialogVisible(true);
        this.particlesView.setPaused(true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean canBeginSlide() {
        return !this.backgroundView.imageView.touched;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.PremiumPreviewFragment$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                PremiumPreviewFragment.this.updateColors();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, Theme.key_premiumGradient1, Theme.key_premiumGradient2, Theme.key_premiumGradient3, Theme.key_premiumGradient4, Theme.key_premiumGradientBackground1, Theme.key_premiumGradientBackground2, Theme.key_premiumGradientBackground3, Theme.key_premiumGradientBackground4, Theme.key_premiumGradientBackgroundOverlay, Theme.key_premiumStartGradient1, Theme.key_premiumStartGradient2, Theme.key_premiumStartSmallStarsColor, Theme.key_premiumStartSmallStarsColor2);
    }

    public void updateColors() {
        if (this.backgroundView == null || this.actionBar == null) {
            return;
        }
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_premiumGradientBackgroundOverlay), false);
        this.actionBar.setItemsBackgroundColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_premiumGradientBackgroundOverlay), 60), false);
        this.backgroundView.titleView.setTextColor(Theme.getColor(Theme.key_premiumGradientBackgroundOverlay));
        this.backgroundView.subtitleView.setTextColor(Theme.getColor(Theme.key_premiumGradientBackgroundOverlay));
        this.particlesView.drawable.updateColors();
        if (this.backgroundView.imageView.mRenderer != null) {
            this.backgroundView.imageView.mRenderer.updateColors();
        }
        updateBackgroundImage();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        if (this.settingsView != null) {
            closeSetting();
            return false;
        }
        return super.onBackPressed();
    }

    private void closeSetting() {
        this.settingsView.animate().translationY(AndroidUtilities.dp(1000.0f)).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PremiumPreviewFragment.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PremiumPreviewFragment.this.contentView.removeView(PremiumPreviewFragment.this.settingsView);
                PremiumPreviewFragment.this.settingsView = null;
                super.onAnimationEnd(animation);
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public Dialog showDialog(Dialog dialog) {
        Dialog d = super.showDialog(dialog);
        updateDialogVisibility(d != null);
        return d;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        updateDialogVisibility(false);
    }

    private void updateDialogVisibility(boolean isVisible) {
        if (isVisible != this.isDialogVisible) {
            this.isDialogVisible = isVisible;
            this.backgroundView.imageView.setDialogVisible(isVisible);
            this.particlesView.setPaused(isVisible);
            this.contentView.invalidate();
        }
    }

    private void sentShowScreenStat() {
        String str = this.source;
        if (str == null) {
            return;
        }
        sentShowScreenStat(str);
        this.source = null;
    }

    public static void sentShowScreenStat(String source) {
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        TLRPC.TL_help_saveAppLog req = new TLRPC.TL_help_saveAppLog();
        TLRPC.TL_inputAppEvent event = new TLRPC.TL_inputAppEvent();
        event.time = connectionsManager.getCurrentTime();
        event.type = "premium.promo_screen_show";
        TLRPC.TL_jsonObject data = new TLRPC.TL_jsonObject();
        event.data = data;
        TLRPC.TL_jsonObjectValue sourceObj = new TLRPC.TL_jsonObjectValue();
        TLRPC.TL_jsonString jsonString = new TLRPC.TL_jsonString();
        jsonString.value = source;
        sourceObj.key = Constants.ScionAnalytics.PARAM_SOURCE;
        sourceObj.value = jsonString;
        data.value.add(sourceObj);
        req.events.add(event);
        connectionsManager.sendRequest(req, PremiumPreviewFragment$$ExternalSyntheticLambda11.INSTANCE);
    }

    public static /* synthetic */ void lambda$sentShowScreenStat$7(TLObject response, TLRPC.TL_error error) {
    }

    public static void sentPremiumButtonClick() {
        TLRPC.TL_help_saveAppLog req = new TLRPC.TL_help_saveAppLog();
        TLRPC.TL_inputAppEvent event = new TLRPC.TL_inputAppEvent();
        event.time = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
        event.type = "premium.promo_screen_accept";
        event.data = new TLRPC.TL_jsonNull();
        req.events.add(event);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, PremiumPreviewFragment$$ExternalSyntheticLambda8.INSTANCE);
    }

    public static /* synthetic */ void lambda$sentPremiumButtonClick$8(TLObject response, TLRPC.TL_error error) {
    }

    public static void sentPremiumBuyCanceled() {
        TLRPC.TL_help_saveAppLog req = new TLRPC.TL_help_saveAppLog();
        TLRPC.TL_inputAppEvent event = new TLRPC.TL_inputAppEvent();
        event.time = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
        event.type = "premium.promo_screen_fail";
        event.data = new TLRPC.TL_jsonNull();
        req.events.add(event);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, PremiumPreviewFragment$$ExternalSyntheticLambda9.INSTANCE);
    }

    public static /* synthetic */ void lambda$sentPremiumBuyCanceled$9(TLObject response, TLRPC.TL_error error) {
    }

    public static void sentShowFeaturePreview(int currentAccount, int type) {
        TLRPC.TL_help_saveAppLog req = new TLRPC.TL_help_saveAppLog();
        TLRPC.TL_inputAppEvent event = new TLRPC.TL_inputAppEvent();
        event.time = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
        event.type = "premium.promo_screen_tap";
        TLRPC.TL_jsonObject data = new TLRPC.TL_jsonObject();
        event.data = data;
        TLRPC.TL_jsonObjectValue item = new TLRPC.TL_jsonObjectValue();
        TLRPC.TL_jsonString jsonString = new TLRPC.TL_jsonString();
        jsonString.value = featureTypeToServerString(type);
        item.key = "item";
        item.value = jsonString;
        data.value.add(item);
        req.events.add(event);
        event.data = data;
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, PremiumPreviewFragment$$ExternalSyntheticLambda10.INSTANCE);
    }

    public static /* synthetic */ void lambda$sentShowFeaturePreview$10(TLObject response, TLRPC.TL_error error) {
    }
}
