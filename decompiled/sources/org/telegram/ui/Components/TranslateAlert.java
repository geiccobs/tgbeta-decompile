package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.widget.NestedScrollView;
import com.google.android.exoplayer2.C;
import com.google.firebase.messaging.Constants;
import com.microsoft.appcenter.http.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.TranslateAlert;
/* loaded from: classes5.dex */
public class TranslateAlert extends Dialog {
    private Spannable allTexts;
    private TextView allTextsView;
    private boolean allowScroll;
    private ImageView backButton;
    protected ColorDrawable backDrawable;
    private android.graphics.Rect backRect;
    private int blockIndex;
    private FrameLayout bulletinContainer;
    private android.graphics.Rect buttonRect;
    private FrameLayout buttonShadowView;
    private TextView buttonTextView;
    private FrameLayout buttonView;
    private FrameLayout container;
    private float containerOpenAnimationT;
    private android.graphics.Rect containerRect;
    private FrameLayout contentView;
    private boolean dismissed;
    private boolean fastHide;
    private int firstMinHeight;
    private BaseFragment fragment;
    private String fromLanguage;
    private boolean fromScrollRect;
    private float fromScrollViewY;
    private float fromScrollY;
    private boolean fromTranslateMoreView;
    private float fromY;
    private FrameLayout header;
    private FrameLayout.LayoutParams headerLayout;
    private FrameLayout headerShadowView;
    private float heightMaxPercent;
    private LinkSpanDrawable.LinkCollector links;
    private boolean loaded;
    private boolean loading;
    private boolean maybeScrolling;
    private boolean noforwards;
    private Runnable onDismiss;
    private OnLinkPress onLinkPress;
    private ValueAnimator openAnimationToAnimator;
    private boolean openAnimationToAnimatorPriority;
    private ValueAnimator openingAnimator;
    private boolean openingAnimatorPriority;
    private float openingT;
    private LinkSpanDrawable pressedLink;
    private boolean pressedOutside;
    private android.graphics.Rect scrollRect;
    private NestedScrollView scrollView;
    private FrameLayout.LayoutParams scrollViewLayout;
    private boolean scrolling;
    private ImageView subtitleArrowView;
    private InlineLoadingTextView subtitleFromView;
    private FrameLayout.LayoutParams subtitleLayout;
    private TextView subtitleToView;
    private LinearLayout subtitleView;
    private CharSequence text;
    private ArrayList<CharSequence> textBlocks;
    private android.graphics.Rect textRect;
    private FrameLayout textsContainerView;
    private TextBlocksLayout textsView;
    private FrameLayout.LayoutParams titleLayout;
    private TextView titleView;
    private String toLanguage;
    private android.graphics.Rect translateMoreRect;
    public static volatile DispatchQueue translateQueue = new DispatchQueue("translateQueue", false);
    private static final int MOST_SPEC = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);

    /* loaded from: classes5.dex */
    public interface OnLinkPress {
        boolean run(URLSpan uRLSpan);
    }

    /* loaded from: classes5.dex */
    public interface OnTranslationFail {
        void run(boolean z);
    }

    /* loaded from: classes5.dex */
    public interface OnTranslationSuccess {
        void run(String str, String str2);
    }

    public void openAnimation(float t) {
        float f = 1.0f;
        float t2 = Math.min(Math.max(t, 0.0f), 1.0f);
        if (this.containerOpenAnimationT == t2) {
            return;
        }
        this.containerOpenAnimationT = t2;
        this.titleView.setScaleX(AndroidUtilities.lerp(1.0f, 0.9473f, t2));
        this.titleView.setScaleY(AndroidUtilities.lerp(1.0f, 0.9473f, t2));
        this.titleLayout.setMargins(AndroidUtilities.dp(AndroidUtilities.lerp(22, 72, t2)), AndroidUtilities.dp(AndroidUtilities.lerp(22, 8, t2)), this.titleLayout.rightMargin, this.titleLayout.bottomMargin);
        this.titleView.setLayoutParams(this.titleLayout);
        this.subtitleLayout.setMargins(AndroidUtilities.dp(AndroidUtilities.lerp(22, 72, t2)) - LoadingTextView2.paddingHorizontal, AndroidUtilities.dp(AndroidUtilities.lerp(47, 30, t2)) - LoadingTextView2.paddingVertical, this.subtitleLayout.rightMargin, this.subtitleLayout.bottomMargin);
        this.subtitleView.setLayoutParams(this.subtitleLayout);
        this.backButton.setAlpha(t2);
        this.backButton.setScaleX((t2 * 0.25f) + 0.75f);
        this.backButton.setScaleY((0.25f * t2) + 0.75f);
        this.backButton.setClickable(t2 > 0.5f);
        FrameLayout frameLayout = this.headerShadowView;
        if (this.scrollView.getScrollY() <= 0) {
            f = t2;
        }
        frameLayout.setAlpha(f);
        this.headerLayout.height = AndroidUtilities.lerp(AndroidUtilities.dp(70.0f), AndroidUtilities.dp(56.0f), t2);
        this.header.setLayoutParams(this.headerLayout);
        FrameLayout.LayoutParams layoutParams = this.scrollViewLayout;
        layoutParams.setMargins(layoutParams.leftMargin, AndroidUtilities.lerp(AndroidUtilities.dp(70.0f), AndroidUtilities.dp(56.0f), t2), this.scrollViewLayout.rightMargin, this.scrollViewLayout.bottomMargin);
        this.scrollView.setLayoutParams(this.scrollViewLayout);
    }

    public void openAnimationTo(float to, boolean priority) {
        openAnimationTo(to, priority, null);
    }

    private void openAnimationTo(float to, boolean priority, final Runnable onAnimationEnd) {
        if (this.openAnimationToAnimatorPriority && !priority) {
            return;
        }
        this.openAnimationToAnimatorPriority = priority;
        float to2 = Math.min(Math.max(to, 0.0f), 1.0f);
        ValueAnimator valueAnimator = this.openAnimationToAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.containerOpenAnimationT, to2);
        this.openAnimationToAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                TranslateAlert.this.m3162xab02aaef(valueAnimator2);
            }
        });
        this.openAnimationToAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TranslateAlert.1
            {
                TranslateAlert.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                TranslateAlert.this.openAnimationToAnimatorPriority = false;
                Runnable runnable = onAnimationEnd;
                if (runnable != null) {
                    runnable.run();
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                TranslateAlert.this.openAnimationToAnimatorPriority = false;
            }
        });
        this.openAnimationToAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.openAnimationToAnimator.setDuration(220L);
        this.openAnimationToAnimator.start();
        if (to2 >= 0.5d && this.blockIndex <= 1) {
            fetchNext();
        }
    }

    /* renamed from: lambda$openAnimationTo$0$org-telegram-ui-Components-TranslateAlert */
    public /* synthetic */ void m3162xab02aaef(ValueAnimator a) {
        openAnimation(((Float) a.getAnimatedValue()).floatValue());
    }

    public int minHeight() {
        return minHeight(false);
    }

    private int minHeight(boolean full) {
        TextBlocksLayout textBlocksLayout = this.textsView;
        int textsViewHeight = textBlocksLayout == null ? 0 : textBlocksLayout.getMeasuredHeight();
        int height = AndroidUtilities.dp(147.0f) + textsViewHeight;
        if (this.firstMinHeight < 0 && textsViewHeight > 0) {
            this.firstMinHeight = height;
        }
        if (this.firstMinHeight > 0 && this.textBlocks.size() > 1 && !full) {
            return this.firstMinHeight;
        }
        return height;
    }

    public boolean canExpand() {
        return this.textsView.getBlocksCount() < this.textBlocks.size() || ((float) minHeight(true)) >= ((float) AndroidUtilities.displayMetrics.heightPixels) * this.heightMaxPercent;
    }

    public void updateCanExpand() {
        boolean canExpand = canExpand();
        float f = 0.0f;
        if (this.containerOpenAnimationT > 0.0f && !canExpand) {
            openAnimationTo(0.0f, false);
        }
        ViewPropertyAnimator alpha = this.buttonShadowView.animate().alpha(canExpand ? 1.0f : 0.0f);
        float alpha2 = this.buttonShadowView.getAlpha();
        if (canExpand) {
            f = 1.0f;
        }
        alpha.setDuration(Math.abs(alpha2 - f) * 220.0f).start();
    }

    public TranslateAlert(BaseFragment fragment, Context context, String fromLanguage, String toLanguage, CharSequence text, boolean noforwards, OnLinkPress onLinkPress, Runnable onDismiss) {
        this(fragment, context, -1, null, -1, fromLanguage, toLanguage, text, noforwards, onLinkPress, onDismiss);
    }

    public TranslateAlert(BaseFragment fragment, Context context, int currentAccount, TLRPC.InputPeer peer, int msgId, String fromLanguage, String toLanguage, CharSequence text, boolean noforwards, OnLinkPress onLinkPress, Runnable onDismiss) {
        super(context, R.style.TransparentDialog);
        this.blockIndex = 0;
        this.containerOpenAnimationT = 0.0f;
        this.openAnimationToAnimatorPriority = false;
        String str = null;
        this.openAnimationToAnimator = null;
        this.firstMinHeight = -1;
        this.allowScroll = true;
        this.fromScrollY = 0.0f;
        this.containerRect = new android.graphics.Rect();
        this.textRect = new android.graphics.Rect();
        this.translateMoreRect = new android.graphics.Rect();
        this.buttonRect = new android.graphics.Rect();
        this.backRect = new android.graphics.Rect();
        this.scrollRect = new android.graphics.Rect();
        this.fromY = 0.0f;
        this.pressedOutside = false;
        this.maybeScrolling = false;
        this.scrolling = false;
        this.fromScrollRect = false;
        this.fromTranslateMoreView = false;
        this.fromScrollViewY = 0.0f;
        this.allTexts = null;
        this.openingT = 0.0f;
        this.backDrawable = new ColorDrawable(-16777216) { // from class: org.telegram.ui.Components.TranslateAlert.6
            {
                TranslateAlert.this = this;
            }

            @Override // android.graphics.drawable.ColorDrawable, android.graphics.drawable.Drawable
            public void setAlpha(int alpha) {
                super.setAlpha(alpha);
                TranslateAlert.this.container.invalidate();
            }
        };
        this.dismissed = false;
        this.heightMaxPercent = 0.85f;
        this.fastHide = false;
        this.openingAnimatorPriority = false;
        this.loading = false;
        this.loaded = false;
        if (peer != null) {
            translateText(currentAccount, peer, msgId, (fromLanguage == null || !fromLanguage.equals("und")) ? fromLanguage : str, toLanguage);
        }
        this.onLinkPress = onLinkPress;
        this.noforwards = noforwards;
        this.fragment = fragment;
        this.fromLanguage = (fromLanguage == null || !fromLanguage.equals("und")) ? fromLanguage : "auto";
        this.toLanguage = toLanguage;
        this.text = text;
        this.textBlocks = cutInBlocks(text, 1024);
        this.onDismiss = onDismiss;
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().addFlags(-2147483392);
        } else if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(-2147417856);
        }
        if (noforwards) {
            getWindow().addFlags(8192);
        }
        FrameLayout frameLayout = new FrameLayout(context);
        this.contentView = frameLayout;
        frameLayout.setBackground(this.backDrawable);
        this.contentView.setClipChildren(false);
        this.contentView.setClipToPadding(false);
        if (Build.VERSION.SDK_INT >= 21) {
            this.contentView.setFitsSystemWindows(true);
            if (Build.VERSION.SDK_INT >= 30) {
                this.contentView.setSystemUiVisibility(1792);
            } else {
                this.contentView.setSystemUiVisibility(1280);
            }
        }
        final Paint containerPaint = new Paint();
        containerPaint.setColor(Theme.getColor(Theme.key_dialogBackground));
        containerPaint.setShadowLayer(AndroidUtilities.dp(2.0f), 0.0f, AndroidUtilities.dp(-0.66f), 503316480);
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.Components.TranslateAlert.2
            private int contentHeight = Integer.MAX_VALUE;
            private Path containerPath = new Path();
            private RectF containerRect = new RectF();
            private RectF rectF = new RectF();

            {
                TranslateAlert.this = this;
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int fullWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                View.MeasureSpec.getSize(widthMeasureSpec);
                int minHeight = (int) (AndroidUtilities.displayMetrics.heightPixels * TranslateAlert.this.heightMaxPercent);
                if (TranslateAlert.this.textsView != null && TranslateAlert.this.textsView.getMeasuredHeight() <= 0) {
                    TranslateAlert.this.textsView.measure(View.MeasureSpec.makeMeasureSpec((((View.MeasureSpec.getSize(widthMeasureSpec) - TranslateAlert.this.textsView.getPaddingLeft()) - TranslateAlert.this.textsView.getPaddingRight()) - TranslateAlert.this.textsContainerView.getPaddingLeft()) - TranslateAlert.this.textsContainerView.getPaddingRight(), C.BUFFER_FLAG_ENCRYPTED), 0);
                }
                int fromHeight = Math.min(minHeight, TranslateAlert.this.minHeight());
                int height = (int) (fromHeight + ((AndroidUtilities.displayMetrics.heightPixels - fromHeight) * TranslateAlert.this.containerOpenAnimationT));
                TranslateAlert.this.updateCanExpand();
                super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) Math.max(fullWidth * 0.8f, Math.min(AndroidUtilities.dp(480.0f), fullWidth)), View.MeasureSpec.getMode(widthMeasureSpec)), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                this.contentHeight = Math.min(this.contentHeight, bottom - top);
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                int w = getWidth();
                int h = getHeight();
                int r = AndroidUtilities.dp((1.0f - TranslateAlert.this.containerOpenAnimationT) * 12.0f);
                canvas.clipRect(0, 0, w, h);
                this.containerRect.set(0.0f, 0.0f, w, h + r);
                canvas.translate(0.0f, (1.0f - TranslateAlert.this.openingT) * h);
                canvas.drawRoundRect(this.containerRect, r, r, containerPaint);
                super.onDraw(canvas);
            }
        };
        this.container = frameLayout2;
        frameLayout2.setWillNotDraw(false);
        this.header = new FrameLayout(context);
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setPivotX(LocaleController.isRTL ? this.titleView.getWidth() : 0.0f);
        this.titleView.setPivotY(0.0f);
        this.titleView.setLines(1);
        this.titleView.setText(LocaleController.getString("AutomaticTranslation", R.string.AutomaticTranslation));
        this.titleView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.titleView.setTextSize(0, AndroidUtilities.dp(19.0f));
        FrameLayout frameLayout3 = this.header;
        View view = this.titleView;
        FrameLayout.LayoutParams createFrame = LayoutHelper.createFrame(-1, -2.0f, 55, 22.0f, 22.0f, 22.0f, 0.0f);
        this.titleLayout = createFrame;
        frameLayout3.addView(view, createFrame);
        this.titleView.post(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                TranslateAlert.this.m3159lambda$new$1$orgtelegramuiComponentsTranslateAlert();
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        this.subtitleView = linearLayout;
        linearLayout.setOrientation(0);
        if (Build.VERSION.SDK_INT >= 17) {
            this.subtitleView.setLayoutDirection(LocaleController.isRTL ? 1 : 0);
        }
        this.subtitleView.setGravity(LocaleController.isRTL ? 5 : 3);
        String fromLanguageName = languageName(fromLanguage);
        InlineLoadingTextView inlineLoadingTextView = new InlineLoadingTextView(context, fromLanguageName == null ? languageName(toLanguage) : fromLanguageName, AndroidUtilities.dp(14.0f), Theme.getColor(Theme.key_player_actionBarSubtitle)) { // from class: org.telegram.ui.Components.TranslateAlert.3
            {
                TranslateAlert.this = this;
            }

            @Override // org.telegram.ui.Components.TranslateAlert.InlineLoadingTextView
            protected void onLoadAnimation(float t) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) TranslateAlert.this.subtitleFromView.getLayoutParams();
                if (lp != null) {
                    if (LocaleController.isRTL) {
                        lp.leftMargin = AndroidUtilities.dp(2.0f - (6.0f * t));
                    } else {
                        lp.rightMargin = AndroidUtilities.dp(2.0f - (6.0f * t));
                    }
                    TranslateAlert.this.subtitleFromView.setLayoutParams(lp);
                }
            }
        };
        this.subtitleFromView = inlineLoadingTextView;
        inlineLoadingTextView.showLoadingText = false;
        ImageView imageView = new ImageView(context);
        this.subtitleArrowView = imageView;
        imageView.setImageResource(R.drawable.search_arrow);
        this.subtitleArrowView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_actionBarSubtitle), PorterDuff.Mode.MULTIPLY));
        if (LocaleController.isRTL) {
            this.subtitleArrowView.setScaleX(-1.0f);
        }
        TextView textView2 = new TextView(context);
        this.subtitleToView = textView2;
        textView2.setLines(1);
        this.subtitleToView.setTextColor(Theme.getColor(Theme.key_player_actionBarSubtitle));
        this.subtitleToView.setTextSize(0, AndroidUtilities.dp(14.0f));
        this.subtitleToView.setText(languageName(toLanguage));
        if (LocaleController.isRTL) {
            this.subtitleView.setPadding(InlineLoadingTextView.paddingHorizontal, 0, 0, 0);
            this.subtitleView.addView(this.subtitleToView, LayoutHelper.createLinear(-2, -2, 16));
            this.subtitleView.addView(this.subtitleArrowView, LayoutHelper.createLinear(-2, -2, 16, 3, 1, 0, 0));
            this.subtitleView.addView(this.subtitleFromView, LayoutHelper.createLinear(-2, -2, 16, 2, 0, 0, 0));
        } else {
            this.subtitleView.setPadding(0, 0, InlineLoadingTextView.paddingHorizontal, 0);
            this.subtitleView.addView(this.subtitleFromView, LayoutHelper.createLinear(-2, -2, 16, 0, 0, 2, 0));
            this.subtitleView.addView(this.subtitleArrowView, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 3, 0));
            this.subtitleView.addView(this.subtitleToView, LayoutHelper.createLinear(-2, -2, 16));
        }
        if (fromLanguageName != null) {
            this.subtitleFromView.set(fromLanguageName);
        }
        FrameLayout frameLayout4 = this.header;
        View view2 = this.subtitleView;
        FrameLayout.LayoutParams createFrame2 = LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f - (LoadingTextView2.paddingHorizontal / AndroidUtilities.density), 47.0f - (LoadingTextView2.paddingVertical / AndroidUtilities.density), 22.0f - (LoadingTextView2.paddingHorizontal / AndroidUtilities.density), 0.0f);
        this.subtitleLayout = createFrame2;
        frameLayout4.addView(view2, createFrame2);
        ImageView imageView2 = new ImageView(context);
        this.backButton = imageView2;
        imageView2.setImageResource(R.drawable.ic_ab_back);
        this.backButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlack), PorterDuff.Mode.MULTIPLY));
        this.backButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        this.backButton.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        this.backButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector)));
        this.backButton.setClickable(false);
        this.backButton.setAlpha(0.0f);
        this.backButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                TranslateAlert.this.m3160lambda$new$2$orgtelegramuiComponentsTranslateAlert(view3);
            }
        });
        this.header.addView(this.backButton, LayoutHelper.createFrame(56, 56, 3));
        FrameLayout frameLayout5 = new FrameLayout(context);
        this.headerShadowView = frameLayout5;
        frameLayout5.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.headerShadowView.setAlpha(0.0f);
        this.header.addView(this.headerShadowView, LayoutHelper.createFrame(-1, 1, 87));
        this.header.setClipChildren(false);
        FrameLayout frameLayout6 = this.container;
        View view3 = this.header;
        FrameLayout.LayoutParams createFrame3 = LayoutHelper.createFrame(-1, 70, 55);
        this.headerLayout = createFrame3;
        frameLayout6.addView(view3, createFrame3);
        NestedScrollView nestedScrollView = new NestedScrollView(context) { // from class: org.telegram.ui.Components.TranslateAlert.4
            {
                TranslateAlert.this = this;
            }

            @Override // androidx.core.widget.NestedScrollView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return TranslateAlert.this.allowScroll && TranslateAlert.this.containerOpenAnimationT >= 1.0f && TranslateAlert.this.canExpand() && super.onInterceptTouchEvent(ev);
            }

            @Override // androidx.core.widget.NestedScrollView, android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
            public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
                super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
            }

            @Override // androidx.core.widget.NestedScrollView, android.view.View
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                super.onScrollChanged(l, t, oldl, oldt);
                if (TranslateAlert.this.checkForNextLoading()) {
                    TranslateAlert.this.openAnimationTo(1.0f, true);
                }
            }
        };
        this.scrollView = nestedScrollView;
        nestedScrollView.setClipChildren(true);
        this.allTextsView = new TextView(context) { // from class: org.telegram.ui.Components.TranslateAlert.5
            {
                TranslateAlert.this = this;
            }

            @Override // android.widget.TextView, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, TranslateAlert.MOST_SPEC);
            }

            @Override // android.widget.TextView, android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.translate(getPaddingLeft(), getPaddingTop());
                if (TranslateAlert.this.links != null && TranslateAlert.this.links.draw(canvas)) {
                    invalidate();
                }
            }

            @Override // android.widget.TextView
            public boolean onTextContextMenuItem(int id) {
                if (id == 16908321 && isFocused()) {
                    ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                    ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, getText().subSequence(Math.max(0, Math.min(getSelectionStart(), getSelectionEnd())), Math.max(0, Math.max(getSelectionStart(), getSelectionEnd()))));
                    clipboard.setPrimaryClip(clip);
                    BulletinFactory.of(TranslateAlert.this.bulletinContainer, null).createCopyBulletin(LocaleController.getString("TextCopied", R.string.TextCopied)).show();
                    clearFocus();
                    return true;
                }
                return super.onTextContextMenuItem(id);
            }
        };
        this.links = new LinkSpanDrawable.LinkCollector(this.allTextsView);
        this.allTextsView.setTextColor(0);
        this.allTextsView.setTextSize(1, 16.0f);
        this.allTextsView.setTextIsSelectable(!noforwards);
        this.allTextsView.setHighlightColor(Theme.getColor(Theme.key_chat_inTextSelectionHighlight));
        int handleColor = Theme.getColor(Theme.key_chat_TextSelectionCursor);
        try {
            if (Build.VERSION.SDK_INT >= 29 && !XiaomiUtilities.isMIUI()) {
                Drawable left = this.allTextsView.getTextSelectHandleLeft();
                left.setColorFilter(handleColor, PorterDuff.Mode.SRC_IN);
                this.allTextsView.setTextSelectHandleLeft(left);
                Drawable right = this.allTextsView.getTextSelectHandleRight();
                right.setColorFilter(handleColor, PorterDuff.Mode.SRC_IN);
                this.allTextsView.setTextSelectHandleRight(right);
            }
        } catch (Exception e) {
        }
        this.allTextsView.setFocusable(true);
        this.allTextsView.setMovementMethod(new LinkMovementMethod());
        TextBlocksLayout textBlocksLayout = new TextBlocksLayout(context, AndroidUtilities.dp(16.0f), Theme.getColor(Theme.key_dialogTextBlack), this.allTextsView);
        this.textsView = textBlocksLayout;
        textBlocksLayout.setPadding(AndroidUtilities.dp(22.0f) - LoadingTextView2.paddingHorizontal, AndroidUtilities.dp(12.0f) - LoadingTextView2.paddingVertical, AndroidUtilities.dp(22.0f) - LoadingTextView2.paddingHorizontal, AndroidUtilities.dp(12.0f) - LoadingTextView2.paddingVertical);
        Iterator<CharSequence> it = this.textBlocks.iterator();
        while (it.hasNext()) {
            CharSequence blockText = it.next();
            this.textsView.addBlock(blockText);
        }
        FrameLayout frameLayout7 = new FrameLayout(context);
        this.textsContainerView = frameLayout7;
        frameLayout7.addView(this.textsView, LayoutHelper.createFrame(-1, -2.0f));
        this.scrollView.addView(this.textsContainerView, LayoutHelper.createLinear(-1, -2, 1.0f));
        FrameLayout frameLayout8 = this.container;
        View view4 = this.scrollView;
        FrameLayout.LayoutParams createFrame4 = LayoutHelper.createFrame(-1, -2.0f, 119, 0.0f, 70.0f, 0.0f, 81.0f);
        this.scrollViewLayout = createFrame4;
        frameLayout8.addView(view4, createFrame4);
        fetchNext();
        FrameLayout frameLayout9 = new FrameLayout(context);
        this.buttonShadowView = frameLayout9;
        frameLayout9.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.container.addView(this.buttonShadowView, LayoutHelper.createFrame(-1, 1.0f, 87, 0.0f, 0.0f, 0.0f, 80.0f));
        TextView textView3 = new TextView(context);
        this.buttonTextView = textView3;
        textView3.setLines(1);
        this.buttonTextView.setSingleLine(true);
        this.buttonTextView.setGravity(1);
        this.buttonTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setText(LocaleController.getString("CloseTranslation", R.string.CloseTranslation));
        FrameLayout frameLayout10 = new FrameLayout(context);
        this.buttonView = frameLayout10;
        frameLayout10.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor(Theme.key_featuredStickers_addButton), 4.0f));
        this.buttonView.addView(this.buttonTextView);
        this.buttonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view5) {
                TranslateAlert.this.m3161lambda$new$3$orgtelegramuiComponentsTranslateAlert(view5);
            }
        });
        this.container.addView(this.buttonView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 16.0f, 16.0f, 16.0f));
        this.contentView.addView(this.container, LayoutHelper.createFrame(-1, -2, 81));
        FrameLayout frameLayout11 = new FrameLayout(context);
        this.bulletinContainer = frameLayout11;
        this.contentView.addView(frameLayout11, LayoutHelper.createFrame(-1, -1.0f, 119, 0.0f, 0.0f, 0.0f, 81.0f));
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-TranslateAlert */
    public /* synthetic */ void m3159lambda$new$1$orgtelegramuiComponentsTranslateAlert() {
        this.titleView.setPivotX(LocaleController.isRTL ? this.titleView.getWidth() : 0.0f);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-TranslateAlert */
    public /* synthetic */ void m3160lambda$new$2$orgtelegramuiComponentsTranslateAlert(View e) {
        dismiss();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-TranslateAlert */
    public /* synthetic */ void m3161lambda$new$3$orgtelegramuiComponentsTranslateAlert(View e) {
        dismiss();
    }

    public void showDim(boolean enable) {
        this.contentView.setBackground(enable ? this.backDrawable : null);
    }

    private boolean scrollAtBottom() {
        NestedScrollView nestedScrollView = this.scrollView;
        View view = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
        int bottom = view.getBottom();
        LoadingTextView2 lastUnloadedBlock = this.textsView.getFirstUnloadedBlock();
        if (lastUnloadedBlock != null) {
            bottom = lastUnloadedBlock.getTop();
        }
        int diff = bottom - (this.scrollView.getHeight() + this.scrollView.getScrollY());
        return diff <= this.textsContainerView.getPaddingBottom();
    }

    private void setScrollY(float t) {
        openAnimation(t);
        float max = Math.max(Math.min(t + 1.0f, 1.0f), 0.0f);
        this.openingT = max;
        this.backDrawable.setAlpha((int) (max * 51.0f));
        this.container.invalidate();
        this.bulletinContainer.setTranslationY((1.0f - this.openingT) * Math.min(minHeight(), AndroidUtilities.displayMetrics.heightPixels * this.heightMaxPercent));
    }

    private void scrollYTo(float t) {
        scrollYTo(t, null);
    }

    private void scrollYTo(float t, Runnable onAnimationEnd) {
        openAnimationTo(t, false, onAnimationEnd);
        openTo(1.0f + t, false);
    }

    private float getScrollY() {
        return Math.max(Math.min(this.containerOpenAnimationT - (1.0f - this.openingT), 1.0f), 0.0f);
    }

    private boolean hasSelection() {
        return this.allTextsView.hasSelection();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent event) {
        float f;
        float f2;
        ClickableSpan[] linkSpans;
        try {
            float x = event.getX();
            float y = event.getY();
            this.container.getGlobalVisibleRect(this.containerRect);
            if (!this.containerRect.contains((int) x, (int) y)) {
                if (event.getAction() == 0) {
                    this.pressedOutside = true;
                    return true;
                } else if (event.getAction() == 1 && this.pressedOutside) {
                    this.pressedOutside = false;
                    dismiss();
                    return true;
                }
            }
            try {
                this.allTextsView.getGlobalVisibleRect(this.textRect);
                if (this.textRect.contains((int) x, (int) y) && !this.maybeScrolling) {
                    Layout allTextsLayout = this.allTextsView.getLayout();
                    int tx = (int) ((x - this.allTextsView.getLeft()) - this.container.getLeft());
                    int ty = (int) ((((y - this.allTextsView.getTop()) - this.container.getTop()) - this.scrollView.getTop()) + this.scrollView.getScrollY());
                    int line = allTextsLayout.getLineForVertical(ty);
                    int off = allTextsLayout.getOffsetForHorizontal(line, tx);
                    float left = allTextsLayout.getLineLeft(line);
                    if ((this.allTexts instanceof Spannable) && left <= tx && allTextsLayout.getLineWidth(line) + left >= tx && (linkSpans = (ClickableSpan[]) this.allTexts.getSpans(off, off, ClickableSpan.class)) != null && linkSpans.length >= 1) {
                        if (event.getAction() == 1 && this.pressedLink.getSpan() == linkSpans[0]) {
                            ((ClickableSpan) this.pressedLink.getSpan()).onClick(this.allTextsView);
                            LinkSpanDrawable.LinkCollector linkCollector = this.links;
                            if (linkCollector != null) {
                                linkCollector.removeLink(this.pressedLink);
                            }
                            this.pressedLink = null;
                            this.allTextsView.setTextIsSelectable(!this.noforwards);
                        } else if (event.getAction() == 0) {
                            LinkSpanDrawable linkSpanDrawable = new LinkSpanDrawable(linkSpans[0], this.fragment.getResourceProvider(), tx, ty, false);
                            this.pressedLink = linkSpanDrawable;
                            LinkSpanDrawable.LinkCollector linkCollector2 = this.links;
                            if (linkCollector2 != null) {
                                linkCollector2.addLink(linkSpanDrawable);
                            }
                            LinkPath path = this.pressedLink.obtainNewPath();
                            int start = this.allTexts.getSpanStart(this.pressedLink.getSpan());
                            int end = this.allTexts.getSpanEnd(this.pressedLink.getSpan());
                            path.setCurrentLayout(allTextsLayout, start, 0.0f);
                            allTextsLayout.getSelectionPath(start, end, path);
                        }
                        this.allTextsView.invalidate();
                        return true;
                    }
                }
                if (this.pressedLink != null) {
                    LinkSpanDrawable.LinkCollector linkCollector3 = this.links;
                    if (linkCollector3 != null) {
                        linkCollector3.clear();
                    }
                    this.pressedLink = null;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            this.scrollView.getGlobalVisibleRect(this.scrollRect);
            this.backButton.getGlobalVisibleRect(this.backRect);
            this.buttonView.getGlobalVisibleRect(this.buttonRect);
            if (this.pressedLink == null && !hasSelection()) {
                if (!this.backRect.contains((int) x, (int) y) && !this.buttonRect.contains((int) x, (int) y) && event.getAction() == 0) {
                    this.fromScrollRect = this.scrollRect.contains((int) x, (int) y) && (this.containerOpenAnimationT > 0.0f || !canExpand());
                    this.maybeScrolling = true;
                    this.scrolling = this.scrollRect.contains((int) x, (int) y) && this.textsView.getBlocksCount() > 0 && !this.textsView.getBlockAt(0).loaded;
                    this.fromY = y;
                    this.fromScrollY = getScrollY();
                    this.fromScrollViewY = this.scrollView.getScrollY();
                    super.dispatchTouchEvent(event);
                    return true;
                } else if (this.maybeScrolling && (event.getAction() == 2 || event.getAction() == 1)) {
                    float dy = this.fromY - y;
                    if (this.fromScrollRect) {
                        dy = -Math.max(0.0f, (-(this.fromScrollViewY + AndroidUtilities.dp(48.0f))) - dy);
                        if (dy < 0.0f) {
                            this.scrolling = true;
                            this.allTextsView.setTextIsSelectable(false);
                        }
                    } else if (Math.abs(dy) > AndroidUtilities.dp(4.0f) && !this.fromScrollRect) {
                        this.scrolling = true;
                        this.allTextsView.setTextIsSelectable(false);
                        this.scrollView.stopNestedScroll();
                        this.allowScroll = false;
                    }
                    float fullHeight = AndroidUtilities.displayMetrics.heightPixels;
                    float minHeight = Math.min(minHeight(), this.heightMaxPercent * fullHeight);
                    float f3 = -1.0f;
                    float scrollYPx = ((1.0f - (-Math.min(Math.max(this.fromScrollY, -1.0f), 0.0f))) * minHeight) + ((fullHeight - minHeight) * Math.min(1.0f, Math.max(this.fromScrollY, 0.0f))) + dy;
                    float scrollY = scrollYPx > minHeight ? (scrollYPx - minHeight) / (fullHeight - minHeight) : -(1.0f - (scrollYPx / minHeight));
                    if (!canExpand()) {
                        scrollY = Math.min(scrollY, 0.0f);
                    }
                    updateCanExpand();
                    if (this.scrolling) {
                        setScrollY(scrollY);
                        if (event.getAction() == 1) {
                            this.scrolling = false;
                            this.allTextsView.setTextIsSelectable(!this.noforwards);
                            this.maybeScrolling = false;
                            this.allowScroll = true;
                            if (Math.abs(dy) > AndroidUtilities.dp(16.0f)) {
                                float round = Math.round(this.fromScrollY);
                                if (scrollY > this.fromScrollY) {
                                    f3 = 1.0f;
                                }
                                f = round + (f3 * ((float) Math.ceil(Math.abs(f2 - scrollY))));
                            } else {
                                f = Math.round(this.fromScrollY);
                            }
                            scrollYTo(f, new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda12
                                @Override // java.lang.Runnable
                                public final void run() {
                                    TranslateAlert.this.m3154x59187c0d();
                                }
                            });
                        }
                        return true;
                    }
                }
            }
            if (hasSelection() && this.maybeScrolling) {
                this.scrolling = false;
                this.allTextsView.setTextIsSelectable(!this.noforwards);
                this.maybeScrolling = false;
                this.allowScroll = true;
                scrollYTo(Math.round(this.fromScrollY));
            }
            return super.dispatchTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return super.dispatchTouchEvent(event);
        }
    }

    /* renamed from: lambda$dispatchTouchEvent$4$org-telegram-ui-Components-TranslateAlert */
    public /* synthetic */ void m3154x59187c0d() {
        this.contentView.post(new TranslateAlert$$ExternalSyntheticLambda1(this));
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean z = false;
        this.contentView.setPadding(0, 0, 0, 0);
        setContentView(this.contentView, new ViewGroup.LayoutParams(-1, -1));
        Window window = getWindow();
        window.setWindowAnimations(R.style.DialogNoAnimation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.gravity = 51;
        params.dimAmount = 0.0f;
        params.flags &= -3;
        params.flags |= 131072;
        if (Build.VERSION.SDK_INT >= 21) {
            params.flags |= -2147417856;
        }
        params.flags |= 256;
        params.height = -1;
        window.setAttributes(params);
        int navigationbarColor = Theme.getColor(Theme.key_windowBackgroundWhite);
        AndroidUtilities.setNavigationBarColor(window, navigationbarColor);
        if (AndroidUtilities.computePerceivedBrightness(navigationbarColor) > 0.721d) {
            z = true;
        }
        AndroidUtilities.setLightNavigationBar(window, z);
        this.container.forceLayout();
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
        openAnimation(0.0f);
        openTo(1.0f, true, true);
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        openTo(0.0f, true);
    }

    private void openTo(float t, boolean priority) {
        openTo(t, priority, false);
    }

    private void openTo(float t) {
        openTo(t, false);
    }

    private void openTo(float t, boolean priority, final boolean setAfter) {
        Runnable runnable;
        final float T = Math.min(Math.max(t, 0.0f), 1.0f);
        if (this.openingAnimatorPriority && !priority) {
            return;
        }
        this.openingAnimatorPriority = priority;
        ValueAnimator valueAnimator = this.openingAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.openingAnimator = ValueAnimator.ofFloat(this.openingT, T);
        this.backDrawable.setAlpha((int) (this.openingT * 51.0f));
        this.openingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                TranslateAlert.this.m3163lambda$openTo$5$orgtelegramuiComponentsTranslateAlert(valueAnimator2);
            }
        });
        if (T <= 0.0f && (runnable = this.onDismiss) != null) {
            runnable.run();
        }
        this.openingAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TranslateAlert.7
            {
                TranslateAlert.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (T <= 0.0f) {
                    TranslateAlert.this.dismissInternal();
                } else if (setAfter) {
                    TranslateAlert.this.allTextsView.setTextIsSelectable(!TranslateAlert.this.noforwards);
                    TranslateAlert.this.allTextsView.invalidate();
                    TranslateAlert.this.scrollView.stopNestedScroll();
                    TranslateAlert.this.openAnimation(T - 1.0f);
                }
                TranslateAlert.this.openingAnimatorPriority = false;
            }
        });
        this.openingAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.openingAnimator.setDuration(Math.abs(this.openingT - T) * (this.fastHide ? 200 : 380));
        this.openingAnimator.setStartDelay(setAfter ? 60L : 0L);
        this.openingAnimator.start();
    }

    /* renamed from: lambda$openTo$5$org-telegram-ui-Components-TranslateAlert */
    public /* synthetic */ void m3163lambda$openTo$5$orgtelegramuiComponentsTranslateAlert(ValueAnimator a) {
        this.openingT = ((Float) a.getAnimatedValue()).floatValue();
        this.container.invalidate();
        this.backDrawable.setAlpha((int) (this.openingT * 51.0f));
        this.bulletinContainer.setTranslationY((1.0f - this.openingT) * Math.min(minHeight(), AndroidUtilities.displayMetrics.heightPixels * this.heightMaxPercent));
    }

    public void dismissInternal() {
        try {
            super.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public String languageName(String locale) {
        if (locale == null || locale.equals("und") || locale.equals("auto")) {
            return null;
        }
        LocaleController.LocaleInfo thisLanguageInfo = LocaleController.getInstance().getBuiltinLanguageByPlural(locale);
        LocaleController.LocaleInfo currentLanguageInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        if (thisLanguageInfo == null) {
            return null;
        }
        boolean isCurrentLanguageEnglish = currentLanguageInfo != null && "en".equals(currentLanguageInfo.pluralLangCode);
        if (isCurrentLanguageEnglish) {
            return thisLanguageInfo.nameEnglish;
        }
        return thisLanguageInfo.name;
    }

    public void updateSourceLanguage() {
        if (languageName(this.fromLanguage) != null) {
            this.subtitleView.setAlpha(1.0f);
            if (!this.subtitleFromView.loaded) {
                this.subtitleFromView.loaded(languageName(this.fromLanguage));
            }
        } else if (this.loaded) {
            this.subtitleView.animate().alpha(0.0f).setDuration(150L).start();
        }
    }

    private ArrayList<CharSequence> cutInBlocks(CharSequence full, int maxBlockSize) {
        ArrayList<CharSequence> blocks = new ArrayList<>();
        if (full == null) {
            return blocks;
        }
        while (full.length() > maxBlockSize) {
            String maxBlockStr = full.subSequence(0, maxBlockSize).toString();
            int n = -1;
            if (-1 == -1) {
                n = maxBlockStr.lastIndexOf("\n\n");
            }
            if (n == -1) {
                n = maxBlockStr.lastIndexOf("\n");
            }
            if (n == -1) {
                n = maxBlockStr.lastIndexOf(". ");
            }
            if (n == -1) {
                n = Math.min(maxBlockStr.length(), maxBlockSize);
            }
            blocks.add(full.subSequence(0, n + 1));
            full = full.subSequence(n + 1, full.length());
        }
        if (full.length() > 0) {
            blocks.add(full);
        }
        return blocks;
    }

    private boolean fetchNext() {
        if (this.loading) {
            return false;
        }
        this.loading = true;
        if (this.blockIndex >= this.textBlocks.size()) {
            return false;
        }
        fetchTranslation(this.textBlocks.get(this.blockIndex), Math.min((this.blockIndex + 1) * 1000, 3500), new OnTranslationSuccess() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.TranslateAlert.OnTranslationSuccess
            public final void run(String str, String str2) {
                TranslateAlert.this.m3156lambda$fetchNext$7$orgtelegramuiComponentsTranslateAlert(str, str2);
            }
        }, new OnTranslationFail() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.TranslateAlert.OnTranslationFail
            public final void run(boolean z) {
                TranslateAlert.this.m3157lambda$fetchNext$8$orgtelegramuiComponentsTranslateAlert(z);
            }
        });
        return true;
    }

    /* renamed from: lambda$fetchNext$7$org-telegram-ui-Components-TranslateAlert */
    public /* synthetic */ void m3156lambda$fetchNext$7$orgtelegramuiComponentsTranslateAlert(String translatedText, String sourceLanguage) {
        TextView textView;
        this.loaded = true;
        Spannable spannable = new SpannableStringBuilder(translatedText);
        try {
            MessageObject.addUrlsByPattern(false, spannable, false, 0, 0, true);
            URLSpan[] urlSpans = (URLSpan[]) spannable.getSpans(0, spannable.length(), URLSpan.class);
            for (final URLSpan urlSpan : urlSpans) {
                int start = spannable.getSpanStart(urlSpan);
                int end = spannable.getSpanEnd(urlSpan);
                if (start != -1 && end != -1) {
                    spannable.removeSpan(urlSpan);
                    spannable.setSpan(new ClickableSpan() { // from class: org.telegram.ui.Components.TranslateAlert.8
                        {
                            TranslateAlert.this = this;
                        }

                        @Override // android.text.style.ClickableSpan
                        public void onClick(View view) {
                            if (TranslateAlert.this.onLinkPress != null) {
                                if (TranslateAlert.this.onLinkPress.run(urlSpan)) {
                                    TranslateAlert.this.fastHide = true;
                                    TranslateAlert.this.dismiss();
                                    return;
                                }
                                return;
                            }
                            AlertsCreator.showOpenUrlAlert(TranslateAlert.this.fragment, urlSpan.getURL(), false, false);
                        }

                        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                        public void updateDrawState(TextPaint ds) {
                            int alpha = Math.min(ds.getAlpha(), (ds.getColor() >> 24) & 255);
                            if (!(urlSpan instanceof URLSpanNoUnderline)) {
                                ds.setUnderlineText(true);
                            }
                            ds.setColor(Theme.getColor(Theme.key_dialogTextLink));
                            ds.setAlpha(alpha);
                        }
                    }, start, end, 33);
                }
            }
            AndroidUtilities.addLinks(spannable, 1);
            URLSpan[] urlSpans2 = (URLSpan[]) spannable.getSpans(0, spannable.length(), URLSpan.class);
            for (final URLSpan urlSpan2 : urlSpans2) {
                int start2 = spannable.getSpanStart(urlSpan2);
                int end2 = spannable.getSpanEnd(urlSpan2);
                if (start2 != -1 && end2 != -1) {
                    spannable.removeSpan(urlSpan2);
                    spannable.setSpan(new ClickableSpan() { // from class: org.telegram.ui.Components.TranslateAlert.9
                        {
                            TranslateAlert.this = this;
                        }

                        @Override // android.text.style.ClickableSpan
                        public void onClick(View view) {
                            AlertsCreator.showOpenUrlAlert(TranslateAlert.this.fragment, urlSpan2.getURL(), false, false);
                        }

                        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                        public void updateDrawState(TextPaint ds) {
                            int alpha = Math.min(ds.getAlpha(), (ds.getColor() >> 24) & 255);
                            if (!(urlSpan2 instanceof URLSpanNoUnderline)) {
                                ds.setUnderlineText(true);
                            }
                            ds.setColor(Theme.getColor(Theme.key_dialogTextLink));
                            ds.setAlpha(alpha);
                        }
                    }, start2, end2, 33);
                }
            }
            spannable = (Spannable) Emoji.replaceEmoji(spannable, this.allTextsView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CharSequence charSequence = this.allTexts;
        if (charSequence == null) {
            charSequence = "";
        }
        SpannableStringBuilder allTextsBuilder = new SpannableStringBuilder(charSequence);
        if (this.blockIndex != 0) {
            allTextsBuilder.append((CharSequence) "\n");
        }
        allTextsBuilder.append((CharSequence) spannable);
        this.allTexts = allTextsBuilder;
        this.textsView.setWholeText(allTextsBuilder);
        LoadingTextView2 block = this.textsView.getBlockAt(this.blockIndex);
        if (block != null) {
            block.loaded(spannable, new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    TranslateAlert.this.m3155lambda$fetchNext$6$orgtelegramuiComponentsTranslateAlert();
                }
            });
        }
        if (sourceLanguage != null) {
            this.fromLanguage = sourceLanguage;
            updateSourceLanguage();
        }
        if (this.blockIndex == 0 && AndroidUtilities.isAccessibilityScreenReaderEnabled() && (textView = this.allTextsView) != null) {
            textView.requestFocus();
        }
        this.blockIndex++;
        this.loading = false;
    }

    /* renamed from: lambda$fetchNext$6$org-telegram-ui-Components-TranslateAlert */
    public /* synthetic */ void m3155lambda$fetchNext$6$orgtelegramuiComponentsTranslateAlert() {
        this.contentView.post(new TranslateAlert$$ExternalSyntheticLambda1(this));
    }

    /* renamed from: lambda$fetchNext$8$org-telegram-ui-Components-TranslateAlert */
    public /* synthetic */ void m3157lambda$fetchNext$8$orgtelegramuiComponentsTranslateAlert(boolean rateLimit) {
        if (rateLimit) {
            Toast.makeText(getContext(), LocaleController.getString("TranslationFailedAlert1", R.string.TranslationFailedAlert1), 0).show();
        } else {
            Toast.makeText(getContext(), LocaleController.getString("TranslationFailedAlert2", R.string.TranslationFailedAlert2), 0).show();
        }
        if (this.blockIndex == 0) {
            dismiss();
        }
    }

    public boolean checkForNextLoading() {
        if (scrollAtBottom()) {
            fetchNext();
            return true;
        }
        return false;
    }

    private void fetchTranslation(final CharSequence text, final long minDuration, final OnTranslationSuccess onSuccess, final OnTranslationFail onFail) {
        if (!translateQueue.isAlive()) {
            translateQueue.start();
        }
        translateQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                TranslateAlert.this.m3158x8a9f447c(text, onSuccess, minDuration, onFail);
            }
        });
    }

    /* JADX WARN: Not initialized variable reg: 19, insn: 0x0185: MOVE  (r4 I:??[OBJECT, ARRAY]) = (r19 I:??[OBJECT, ARRAY] A[D('uri' java.lang.String)]), block:B:55:0x0184 */
    /* JADX WARN: Not initialized variable reg: 7, insn: 0x0184: MOVE  (r5 I:??[OBJECT, ARRAY]) = (r7 I:??[OBJECT, ARRAY] A[D('connection' java.net.HttpURLConnection)]), block:B:55:0x0184 */
    /* renamed from: lambda$fetchTranslation$12$org-telegram-ui-Components-TranslateAlert */
    public /* synthetic */ void m3158x8a9f447c(CharSequence text, final OnTranslationSuccess onSuccess, long minDuration, final OnTranslationFail onFail) {
        Exception e;
        boolean z;
        Throwable th;
        HttpURLConnection connection;
        HttpURLConnection connection2 = null;
        long start = SystemClock.elapsedRealtime();
        int i = 0;
        try {
            String uri = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + Uri.encode(this.fromLanguage);
            try {
                connection2 = (HttpURLConnection) new URI((((uri + "&tl=") + Uri.encode(this.toLanguage)) + "&dt=t&ie=UTF-8&oe=UTF-8&otf=1&ssel=0&tsel=0&kc=7&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&q=") + Uri.encode(text.toString())).toURL().openConnection();
                try {
                    connection2.setRequestMethod(DefaultHttpClient.METHOD_GET);
                    connection2.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
                    connection2.setRequestProperty(DefaultHttpClient.CONTENT_TYPE_KEY, "application/json");
                    StringBuilder textBuilder = new StringBuilder();
                    Reader reader = new BufferedReader(new InputStreamReader(connection2.getInputStream(), Charset.forName("UTF-8")));
                    while (true) {
                        try {
                            try {
                                int c = reader.read();
                                if (c == -1) {
                                    break;
                                }
                                try {
                                    textBuilder.append((char) c);
                                } catch (Throwable th2) {
                                    th = th2;
                                    try {
                                        reader.close();
                                    } catch (Throwable th3) {
                                    }
                                    throw th;
                                }
                            } catch (Throwable th4) {
                                th = th4;
                            }
                        } catch (Exception e2) {
                            e = e2;
                            connection2 = connection;
                        }
                    }
                    reader.close();
                    String jsonString = textBuilder.toString();
                    JSONTokener tokener = new JSONTokener(jsonString);
                    JSONArray array = new JSONArray(tokener);
                    JSONArray array1 = array.getJSONArray(0);
                    String sourceLanguage = null;
                    try {
                        sourceLanguage = array.getString(2);
                    } catch (Exception e3) {
                    }
                    if (sourceLanguage != null) {
                        try {
                            if (sourceLanguage.contains("-")) {
                                sourceLanguage = sourceLanguage.substring(0, sourceLanguage.indexOf("-"));
                            }
                        } catch (Exception e4) {
                            e = e4;
                            Exception e5 = e;
                            try {
                                StringBuilder sb = new StringBuilder();
                                sb.append("failed to translate a text ");
                                String str = null;
                                sb.append(connection2 != null ? Integer.valueOf(connection2.getResponseCode()) : null);
                                sb.append(" ");
                                if (connection2 != null) {
                                    str = connection2.getResponseMessage();
                                }
                                sb.append(str);
                                Log.e("translate", sb.toString());
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                            e5.printStackTrace();
                            if (onFail != null && !this.dismissed) {
                                if (connection2 != null) {
                                    try {
                                        if (connection2.getResponseCode() == 429) {
                                            z = true;
                                            final boolean rateLimit = z;
                                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda10
                                                @Override // java.lang.Runnable
                                                public final void run() {
                                                    TranslateAlert.OnTranslationFail.this.run(rateLimit);
                                                }
                                            });
                                            return;
                                        }
                                    } catch (Exception e6) {
                                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda9
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                TranslateAlert.OnTranslationFail.this.run(false);
                                            }
                                        });
                                        return;
                                    }
                                }
                                z = false;
                                final boolean rateLimit2 = z;
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda10
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        TranslateAlert.OnTranslationFail.this.run(rateLimit2);
                                    }
                                });
                                return;
                            }
                            return;
                        }
                    }
                    StringBuilder result = new StringBuilder();
                    int i2 = 0;
                    while (i2 < array1.length()) {
                        String blockText = array1.getJSONArray(i2).getString(i);
                        if (blockText != null && !blockText.equals("null")) {
                            result.append(blockText);
                        }
                        i2++;
                        i = 0;
                    }
                    int i3 = text.length();
                    if (i3 > 0 && text.charAt(0) == '\n') {
                        result.insert(0, "\n");
                        final String finalResult = result.toString();
                        final String finalSourceLanguage = sourceLanguage;
                        long elapsed = SystemClock.elapsedRealtime() - start;
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda11
                            @Override // java.lang.Runnable
                            public final void run() {
                                TranslateAlert.lambda$fetchTranslation$9(TranslateAlert.OnTranslationSuccess.this, finalResult, finalSourceLanguage);
                            }
                        }, Math.max(0L, minDuration - elapsed));
                    }
                    final String finalResult2 = result.toString();
                    final String finalSourceLanguage2 = sourceLanguage;
                    long elapsed2 = SystemClock.elapsedRealtime() - start;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda11
                        @Override // java.lang.Runnable
                        public final void run() {
                            TranslateAlert.lambda$fetchTranslation$9(TranslateAlert.OnTranslationSuccess.this, finalResult2, finalSourceLanguage2);
                        }
                    }, Math.max(0L, minDuration - elapsed2));
                } catch (Exception e7) {
                    e = e7;
                }
            } catch (Exception e8) {
                e = e8;
            }
        } catch (Exception e9) {
            e = e9;
        }
    }

    public static /* synthetic */ void lambda$fetchTranslation$9(OnTranslationSuccess onSuccess, String finalResult, String finalSourceLanguage) {
        if (onSuccess != null) {
            onSuccess.run(finalResult, finalSourceLanguage);
        }
    }

    private static void translateText(int currentAccount, TLRPC.InputPeer peer, int msg_id, String from_lang, String to_lang) {
        TLRPC.TL_messages_translateText req = new TLRPC.TL_messages_translateText();
        req.peer = peer;
        req.msg_id = msg_id;
        req.flags |= 1;
        if (from_lang != null) {
            req.from_lang = from_lang;
            req.flags |= 4;
        }
        req.to_lang = to_lang;
        try {
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, TranslateAlert$$ExternalSyntheticLambda3.INSTANCE);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ void lambda$translateText$13(TLObject error, TLRPC.TL_error res) {
    }

    public static TranslateAlert showAlert(Context context, BaseFragment fragment, int currentAccount, TLRPC.InputPeer peer, int msgId, String fromLanguage, String toLanguage, CharSequence text, boolean noforwards, OnLinkPress onLinkPress, Runnable onDismiss) {
        TranslateAlert alert = new TranslateAlert(fragment, context, currentAccount, peer, msgId, fromLanguage, toLanguage, text, noforwards, onLinkPress, onDismiss);
        if (fragment != null) {
            if (fragment.getParentActivity() != null) {
                fragment.showDialog(alert);
            }
        } else {
            alert.show();
        }
        return alert;
    }

    public static TranslateAlert showAlert(Context context, BaseFragment fragment, String fromLanguage, String toLanguage, CharSequence text, boolean noforwards, OnLinkPress onLinkPress, Runnable onDismiss) {
        TranslateAlert alert = new TranslateAlert(fragment, context, fromLanguage, toLanguage, text, noforwards, onLinkPress, onDismiss);
        if (fragment != null) {
            if (fragment.getParentActivity() != null) {
                fragment.showDialog(alert);
            }
        } else {
            alert.show();
        }
        return alert;
    }

    /* loaded from: classes5.dex */
    public static class TextBlocksLayout extends ViewGroup {
        private static final int gap = ((-LoadingTextView2.paddingVertical) * 4) + AndroidUtilities.dp(0.48f);
        private final int fontSize;
        private final int textColor;
        private TextView wholeTextView;

        public TextBlocksLayout(Context context, int fontSize, int textColor, TextView wholeTextView) {
            super(context);
            this.fontSize = fontSize;
            this.textColor = textColor;
            if (wholeTextView != null) {
                wholeTextView.setPadding(LoadingTextView2.paddingHorizontal, LoadingTextView2.paddingVertical, LoadingTextView2.paddingHorizontal, LoadingTextView2.paddingVertical);
                this.wholeTextView = wholeTextView;
                addView(wholeTextView);
            }
        }

        public void setWholeText(CharSequence wholeText) {
            this.wholeTextView.clearFocus();
            this.wholeTextView.setText(wholeText);
        }

        public LoadingTextView2 addBlock(CharSequence fromText) {
            LoadingTextView2 textView = new LoadingTextView2(getContext(), fromText, getBlocksCount() > 0, this.fontSize, this.textColor);
            textView.setFocusable(false);
            addView(textView);
            TextView textView2 = this.wholeTextView;
            if (textView2 != null) {
                textView2.bringToFront();
            }
            return textView;
        }

        public int getBlocksCount() {
            return getChildCount() - (this.wholeTextView != null ? 1 : 0);
        }

        public LoadingTextView2 getBlockAt(int i) {
            View child = getChildAt(i);
            if (child instanceof LoadingTextView2) {
                return (LoadingTextView2) child;
            }
            return null;
        }

        public LoadingTextView2 getFirstUnloadedBlock() {
            int count = getBlocksCount();
            for (int i = 0; i < count; i++) {
                LoadingTextView2 block = getBlockAt(i);
                if (block != null && !block.loaded) {
                    return block;
                }
            }
            return null;
        }

        public int height() {
            int height = 0;
            int count = getBlocksCount();
            for (int i = 0; i < count; i++) {
                height += getBlockAt(i).height();
            }
            int i2 = getPaddingTop();
            return i2 + height + getPaddingBottom();
        }

        protected void onHeightUpdated(int height) {
        }

        public void updateHeight() {
            boolean updated;
            int newHeight = height();
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
            if (lp == null) {
                lp = new FrameLayout.LayoutParams(-1, newHeight);
                updated = true;
            } else {
                updated = lp.height != newHeight;
                lp.height = newHeight;
            }
            if (updated) {
                setLayoutParams(lp);
                onHeightUpdated(newHeight);
            }
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int count = getBlocksCount();
            int innerWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()) - getPaddingRight(), View.MeasureSpec.getMode(widthMeasureSpec));
            for (int i = 0; i < count; i++) {
                LoadingTextView2 block = getBlockAt(i);
                block.measure(innerWidthMeasureSpec, TranslateAlert.MOST_SPEC);
            }
            int i2 = height();
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(i2, C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int y = 0;
            int count = getBlocksCount();
            int i = 0;
            while (i < count) {
                LoadingTextView2 block = getBlockAt(i);
                int blockHeight = block.height();
                int translationY = i > 0 ? gap : 0;
                block.layout(getPaddingLeft(), getPaddingTop() + y + translationY, (r - l) - getPaddingRight(), getPaddingTop() + y + blockHeight + translationY);
                y += blockHeight;
                if (i > 0 && i < count - 1) {
                    y += gap;
                }
                i++;
            }
            this.wholeTextView.measure(View.MeasureSpec.makeMeasureSpec(((r - l) - getPaddingLeft()) - getPaddingRight(), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(((b - t) - getPaddingTop()) - getPaddingBottom(), C.BUFFER_FLAG_ENCRYPTED));
            this.wholeTextView.layout(getPaddingLeft(), getPaddingTop(), (r - l) - getPaddingRight(), getPaddingTop() + this.wholeTextView.getMeasuredHeight());
        }
    }

    /* loaded from: classes5.dex */
    public static class InlineLoadingTextView extends ViewGroup {
        public static final int paddingHorizontal = AndroidUtilities.dp(6.0f);
        public static final int paddingVertical = 0;
        private final TextView fromTextView;
        private final float gradientWidth;
        private final ValueAnimator loadingAnimator;
        private final Paint loadingPaint;
        private final TextView toTextView;
        public boolean showLoadingText = true;
        private final long start = SystemClock.elapsedRealtime();
        public boolean loaded = false;
        public float loadingT = 0.0f;
        private ValueAnimator loadedAnimator = null;
        private final RectF rect = new RectF();
        private final Path inPath = new Path();
        private final Path tempPath = new Path();
        private final Path loadingPath = new Path();
        private final Path shadePath = new Path();

        public InlineLoadingTextView(Context context, CharSequence fromText, int fontSize, int textColor) {
            super(context);
            Paint paint = new Paint();
            this.loadingPaint = paint;
            float dp = AndroidUtilities.dp(350.0f);
            this.gradientWidth = dp;
            int i = paddingHorizontal;
            setPadding(i, 0, i, 0);
            setClipChildren(false);
            setWillNotDraw(false);
            TextView textView = new TextView(context) { // from class: org.telegram.ui.Components.TranslateAlert.InlineLoadingTextView.1
                {
                    InlineLoadingTextView.this = this;
                }

                @Override // android.widget.TextView, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(TranslateAlert.MOST_SPEC, TranslateAlert.MOST_SPEC);
                }
            };
            this.fromTextView = textView;
            textView.setTextSize(0, fontSize);
            textView.setTextColor(textColor);
            textView.setText(fromText);
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setEllipsize(null);
            textView.setFocusable(false);
            textView.setImportantForAccessibility(2);
            addView(textView);
            TextView textView2 = new TextView(context) { // from class: org.telegram.ui.Components.TranslateAlert.InlineLoadingTextView.2
                {
                    InlineLoadingTextView.this = this;
                }

                @Override // android.widget.TextView, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(TranslateAlert.MOST_SPEC, TranslateAlert.MOST_SPEC);
                }
            };
            this.toTextView = textView2;
            textView2.setTextSize(0, fontSize);
            textView2.setTextColor(textColor);
            textView2.setLines(1);
            textView2.setMaxLines(1);
            textView2.setSingleLine(true);
            textView2.setEllipsize(null);
            textView2.setFocusable(true);
            addView(textView2);
            int c1 = Theme.getColor(Theme.key_dialogBackground);
            int c2 = Theme.getColor(Theme.key_dialogBackgroundGray);
            LinearGradient gradient = new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{c1, c2, c1}, new float[]{0.0f, 0.67f, 1.0f}, Shader.TileMode.REPEAT);
            paint.setShader(gradient);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.loadingAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TranslateAlert$InlineLoadingTextView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TranslateAlert.InlineLoadingTextView.this.m3165x9271a5cb(valueAnimator);
                }
            });
            ofFloat.setDuration(Long.MAX_VALUE);
            ofFloat.start();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-TranslateAlert$InlineLoadingTextView */
        public /* synthetic */ void m3165x9271a5cb(ValueAnimator a) {
            invalidate();
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.fromTextView.measure(0, 0);
            this.toTextView.measure(0, 0);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.lerp(this.fromTextView.getMeasuredWidth(), this.toTextView.getMeasuredWidth(), this.loadingT) + getPaddingLeft() + getPaddingRight(), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(Math.max(this.fromTextView.getMeasuredHeight(), this.toTextView.getMeasuredHeight()), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            this.fromTextView.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + this.fromTextView.getMeasuredWidth(), getPaddingTop() + this.fromTextView.getMeasuredHeight());
            this.toTextView.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + this.toTextView.getMeasuredWidth(), getPaddingTop() + this.toTextView.getMeasuredHeight());
            updateWidth();
        }

        private void updateWidth() {
            boolean updated;
            int newWidth = AndroidUtilities.lerp(this.fromTextView.getMeasuredWidth(), this.toTextView.getMeasuredWidth(), this.loadingT) + getPaddingLeft() + getPaddingRight();
            int newHeight = Math.max(this.fromTextView.getMeasuredHeight(), this.toTextView.getMeasuredHeight());
            ViewGroup.LayoutParams lp = getLayoutParams();
            if (lp == null) {
                lp = new LinearLayout.LayoutParams(newWidth, newHeight);
                updated = true;
            } else {
                updated = (lp.width == newWidth && lp.height == newHeight) ? false : true;
                lp.width = newWidth;
                lp.height = newHeight;
            }
            if (updated) {
                setLayoutParams(lp);
            }
        }

        protected void onLoadAnimation(float t) {
        }

        public void loaded(CharSequence loadedText) {
            loaded(loadedText, 350L, null);
        }

        public void loaded(CharSequence loadedText, Runnable onLoadEnd) {
            loaded(loadedText, 350L, onLoadEnd);
        }

        public void loaded(CharSequence loadedText, long duration, final Runnable onLoadEnd) {
            this.loaded = true;
            this.toTextView.setText(loadedText);
            if (this.loadingAnimator.isRunning()) {
                this.loadingAnimator.cancel();
            }
            if (this.loadedAnimator == null) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.loadedAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TranslateAlert$InlineLoadingTextView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        TranslateAlert.InlineLoadingTextView.this.m3164x9a76264b(valueAnimator);
                    }
                });
                this.loadedAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TranslateAlert.InlineLoadingTextView.3
                    {
                        InlineLoadingTextView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        Runnable runnable = onLoadEnd;
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                });
                this.loadedAnimator.setDuration(duration);
                this.loadedAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                this.loadedAnimator.start();
            }
        }

        /* renamed from: lambda$loaded$1$org-telegram-ui-Components-TranslateAlert$InlineLoadingTextView */
        public /* synthetic */ void m3164x9a76264b(ValueAnimator a) {
            this.loadingT = ((Float) a.getAnimatedValue()).floatValue();
            updateWidth();
            invalidate();
            onLoadAnimation(this.loadingT);
        }

        public void set(CharSequence loadedText) {
            this.loaded = true;
            this.toTextView.setText(loadedText);
            if (this.loadingAnimator.isRunning()) {
                this.loadingAnimator.cancel();
            }
            ValueAnimator valueAnimator = this.loadedAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.loadedAnimator = null;
            }
            this.loadingT = 1.0f;
            requestLayout();
            updateWidth();
            invalidate();
            onLoadAnimation(1.0f);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float w = getWidth();
            float h = getHeight();
            float cx = LocaleController.isRTL ? Math.max(w / 2.0f, w - 8.0f) : Math.min(w / 2.0f, 8.0f);
            float cy = Math.min(h / 2.0f, 8.0f);
            float R = (float) Math.sqrt(Math.max(Math.max((cx * cx) + (cy * cy), ((w - cx) * (w - cx)) + (cy * cy)), Math.max((cx * cx) + ((h - cy) * (h - cy)), ((w - cx) * (w - cx)) + ((h - cy) * (h - cy)))));
            float r = this.loadingT * R;
            this.inPath.reset();
            this.inPath.addCircle(cx, cy, r, Path.Direction.CW);
            canvas.save();
            canvas.clipPath(this.inPath, Region.Op.DIFFERENCE);
            this.loadingPaint.setAlpha((int) ((1.0f - this.loadingT) * 255.0f));
            float f = this.gradientWidth;
            float f2 = this.gradientWidth;
            float dx = f - (((((float) (SystemClock.elapsedRealtime() - this.start)) / 1000.0f) * f2) % f2);
            this.shadePath.reset();
            this.shadePath.addRect(0.0f, 0.0f, w, h, Path.Direction.CW);
            this.loadingPath.reset();
            this.rect.set(0.0f, 0.0f, w, h);
            this.loadingPath.addRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Path.Direction.CW);
            canvas.clipPath(this.loadingPath);
            canvas.translate(-dx, 0.0f);
            this.shadePath.offset(dx, 0.0f, this.tempPath);
            canvas.drawPath(this.tempPath, this.loadingPaint);
            canvas.translate(dx, 0.0f);
            canvas.restore();
            if (this.showLoadingText && this.fromTextView != null) {
                canvas.save();
                this.rect.set(0.0f, 0.0f, w, h);
                canvas.clipPath(this.inPath, Region.Op.DIFFERENCE);
                canvas.translate(paddingHorizontal, 0.0f);
                canvas.saveLayerAlpha(this.rect, 20, 31);
                this.fromTextView.draw(canvas);
                canvas.restore();
                canvas.restore();
            }
            if (this.toTextView != null) {
                canvas.save();
                canvas.clipPath(this.inPath);
                canvas.translate(paddingHorizontal, 0.0f);
                canvas.saveLayerAlpha(this.rect, (int) (this.loadingT * 255.0f), 31);
                this.toTextView.draw(canvas);
                if (this.loadingT < 1.0f) {
                    canvas.restore();
                }
                canvas.restore();
            }
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return false;
        }
    }

    /* loaded from: classes5.dex */
    public static class LoadingTextView2 extends ViewGroup {
        public static final int paddingHorizontal = AndroidUtilities.dp(6.0f);
        public static final int paddingVertical = AndroidUtilities.dp(1.5f);
        private final TextView fromTextView;
        private final float gradientWidth;
        private final ValueAnimator loadingAnimator;
        private final Paint loadingPaint;
        private final boolean scaleFromZero;
        private float scaleT;
        private final TextView toTextView;
        public boolean showLoadingText = true;
        private final long start = SystemClock.elapsedRealtime();
        public boolean loaded = false;
        private float loadingT = 0.0f;
        private ValueAnimator loadedAnimator = null;
        int lastWidth = 0;
        private RectF fetchedPathRect = new RectF();
        private final RectF rect = new RectF();
        private final Path inPath = new Path();
        private final Path tempPath = new Path();
        private final Path loadingPath = new Path();
        private final Path shadePath = new Path();

        public LoadingTextView2(Context context, CharSequence fromText, final boolean scaleFromZero, int fontSize, int textColor) {
            super(context);
            this.scaleT = 1.0f;
            Paint paint = new Paint();
            this.loadingPaint = paint;
            float dp = AndroidUtilities.dp(350.0f);
            this.gradientWidth = dp;
            int i = paddingHorizontal;
            int i2 = paddingVertical;
            setPadding(i, i2, i, i2);
            setClipChildren(false);
            setWillNotDraw(false);
            setFocusable(false);
            TextView textView = new TextView(context) { // from class: org.telegram.ui.Components.TranslateAlert.LoadingTextView2.1
                {
                    LoadingTextView2.this = this;
                }

                @Override // android.widget.TextView, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, TranslateAlert.MOST_SPEC);
                }
            };
            this.fromTextView = textView;
            textView.setTextSize(0, fontSize);
            textView.setTextColor(textColor);
            textView.setText(fromText);
            textView.setLines(0);
            textView.setMaxLines(0);
            textView.setSingleLine(false);
            textView.setEllipsize(null);
            textView.setFocusable(false);
            textView.setImportantForAccessibility(2);
            addView(textView);
            TextView textView2 = new TextView(context) { // from class: org.telegram.ui.Components.TranslateAlert.LoadingTextView2.2
                {
                    LoadingTextView2.this = this;
                }

                @Override // android.widget.TextView, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, TranslateAlert.MOST_SPEC);
                }
            };
            this.toTextView = textView2;
            textView2.setTextSize(0, fontSize);
            textView2.setTextColor(textColor);
            textView2.setLines(0);
            textView2.setMaxLines(0);
            textView2.setSingleLine(false);
            textView2.setEllipsize(null);
            textView2.setFocusable(false);
            textView2.setImportantForAccessibility(2);
            addView(textView2);
            int c1 = Theme.getColor(Theme.key_dialogBackground);
            int c2 = Theme.getColor(Theme.key_dialogBackgroundGray);
            LinearGradient gradient = new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{c1, c2, c1}, new float[]{0.0f, 0.67f, 1.0f}, Shader.TileMode.REPEAT);
            paint.setShader(gradient);
            this.scaleFromZero = scaleFromZero;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.loadingAnimator = ofFloat;
            if (scaleFromZero) {
                this.scaleT = 0.0f;
            }
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TranslateAlert$LoadingTextView2$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TranslateAlert.LoadingTextView2.this.m3167xfb13368e(scaleFromZero, valueAnimator);
                }
            });
            ofFloat.setDuration(Long.MAX_VALUE);
            ofFloat.start();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-TranslateAlert$LoadingTextView2 */
        public /* synthetic */ void m3167xfb13368e(boolean scaleFromZero, ValueAnimator a) {
            invalidate();
            if (scaleFromZero) {
                boolean scaleTWasNoFull = this.scaleT < 1.0f;
                this.scaleT = Math.min(1.0f, ((float) (SystemClock.elapsedRealtime() - this.start)) / 400.0f);
                if (scaleTWasNoFull) {
                    updateHeight();
                }
            }
        }

        public int innerHeight() {
            return (int) (AndroidUtilities.lerp(this.fromTextView.getMeasuredHeight(), this.toTextView.getMeasuredHeight(), this.loadingT) * this.scaleT);
        }

        public int height() {
            return getPaddingTop() + innerHeight() + getPaddingBottom();
        }

        private void updateHeight() {
            ViewParent parent = getParent();
            if (parent instanceof TextBlocksLayout) {
                ((TextBlocksLayout) parent).updateHeight();
            }
        }

        public void loaded(CharSequence loadedText, final Runnable onLoadEnd) {
            this.loaded = true;
            this.toTextView.setText(loadedText);
            layout();
            if (this.loadingAnimator.isRunning()) {
                this.loadingAnimator.cancel();
            }
            if (this.loadedAnimator == null) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.loadedAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TranslateAlert$LoadingTextView2$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        TranslateAlert.LoadingTextView2.this.m3166xe960660e(valueAnimator);
                    }
                });
                this.loadedAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TranslateAlert.LoadingTextView2.3
                    {
                        LoadingTextView2.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        Runnable runnable = onLoadEnd;
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                });
                this.loadedAnimator.setDuration(350L);
                this.loadedAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                this.loadedAnimator.start();
            }
        }

        /* renamed from: lambda$loaded$1$org-telegram-ui-Components-TranslateAlert$LoadingTextView2 */
        public /* synthetic */ void m3166xe960660e(ValueAnimator a) {
            this.loadingT = ((Float) a.getAnimatedValue()).floatValue();
            updateHeight();
            invalidate();
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int innerWidth = (width - getPaddingLeft()) - getPaddingRight();
            if (this.fromTextView.getMeasuredWidth() <= 0 || this.lastWidth != innerWidth) {
                measureChild(this.fromTextView, innerWidth);
                updateLoadingPath();
            }
            if (this.toTextView.getMeasuredWidth() <= 0 || this.lastWidth != innerWidth) {
                measureChild(this.toTextView, innerWidth);
            }
            this.lastWidth = innerWidth;
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height(), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            layout(((r - l) - getPaddingLeft()) - getPaddingRight(), true);
        }

        private void layout(int width, boolean force) {
            if (this.lastWidth != width || force) {
                this.lastWidth = width;
                layout(width);
            }
        }

        private void layout(int width) {
            measureChild(this.fromTextView, width);
            layoutChild(this.fromTextView, width);
            updateLoadingPath();
            measureChild(this.toTextView, width);
            layoutChild(this.toTextView, width);
            updateHeight();
        }

        private void layout() {
            layout(this.lastWidth);
        }

        private void measureChild(View view, int width) {
            view.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), TranslateAlert.MOST_SPEC);
        }

        private void layoutChild(View view, int width) {
            view.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + width, getPaddingTop() + view.getMeasuredHeight());
        }

        private void updateLoadingPath() {
            Layout loadingLayout;
            TextView textView = this.fromTextView;
            if (textView != null && textView.getMeasuredWidth() > 0) {
                this.loadingPath.reset();
                Layout loadingLayout2 = this.fromTextView.getLayout();
                if (loadingLayout2 != null) {
                    CharSequence text = loadingLayout2.getText();
                    int lineCount = loadingLayout2.getLineCount();
                    int i = 0;
                    while (i < lineCount) {
                        float s = loadingLayout2.getLineLeft(i);
                        float e = loadingLayout2.getLineRight(i);
                        float l = Math.min(s, e);
                        float r = Math.max(s, e);
                        int start = loadingLayout2.getLineStart(i);
                        int end = loadingLayout2.getLineEnd(i);
                        boolean hasNonEmptyChar = false;
                        int j = start;
                        while (true) {
                            if (j < end) {
                                char c = text.charAt(j);
                                if (c == '\n' || c == '\t' || c == ' ') {
                                    j++;
                                } else {
                                    hasNonEmptyChar = true;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (!hasNonEmptyChar) {
                            loadingLayout = loadingLayout2;
                        } else {
                            RectF rectF = this.fetchedPathRect;
                            int i2 = paddingHorizontal;
                            int lineTop = loadingLayout2.getLineTop(i);
                            int i3 = paddingVertical;
                            loadingLayout = loadingLayout2;
                            rectF.set(l - i2, lineTop - i3, i2 + r, loadingLayout2.getLineBottom(i) + i3);
                            this.loadingPath.addRoundRect(this.fetchedPathRect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Path.Direction.CW);
                        }
                        i++;
                        loadingLayout2 = loadingLayout;
                    }
                }
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float w = getWidth();
            float h = getHeight();
            float cx = LocaleController.isRTL ? Math.max(w / 2.0f, w - 8.0f) : Math.min(w / 2.0f, 8.0f);
            float cy = Math.min(h / 2.0f, 8.0f);
            float R = (float) Math.sqrt(Math.max(Math.max((cx * cx) + (cy * cy), ((w - cx) * (w - cx)) + (cy * cy)), Math.max((cx * cx) + ((h - cy) * (h - cy)), ((w - cx) * (w - cx)) + ((h - cy) * (h - cy)))));
            float r = this.loadingT * R;
            this.inPath.reset();
            this.inPath.addCircle(cx, cy, r, Path.Direction.CW);
            canvas.save();
            canvas.clipPath(this.inPath, Region.Op.DIFFERENCE);
            this.loadingPaint.setAlpha((int) ((1.0f - this.loadingT) * 255.0f));
            float f = this.gradientWidth;
            float f2 = this.gradientWidth;
            float dx = f - (((((float) (SystemClock.elapsedRealtime() - this.start)) / 1000.0f) * f2) % f2);
            this.shadePath.reset();
            this.shadePath.addRect(0.0f, 0.0f, w, h, Path.Direction.CW);
            int i = paddingHorizontal;
            int i2 = paddingVertical;
            canvas.translate(i, i2);
            canvas.clipPath(this.loadingPath);
            canvas.translate(-i, -i2);
            canvas.translate(-dx, 0.0f);
            this.shadePath.offset(dx, 0.0f, this.tempPath);
            canvas.drawPath(this.tempPath, this.loadingPaint);
            canvas.translate(dx, 0.0f);
            canvas.restore();
            if (this.showLoadingText && this.fromTextView != null) {
                canvas.save();
                this.rect.set(0.0f, 0.0f, w, h);
                canvas.clipPath(this.inPath, Region.Op.DIFFERENCE);
                canvas.translate(i, i2);
                canvas.saveLayerAlpha(this.rect, 20, 31);
                this.fromTextView.draw(canvas);
                canvas.restore();
                canvas.restore();
            }
            if (this.toTextView != null) {
                canvas.save();
                canvas.clipPath(this.inPath);
                canvas.translate(i, i2);
                canvas.saveLayerAlpha(this.rect, (int) (this.loadingT * 255.0f), 31);
                this.toTextView.draw(canvas);
                if (this.loadingT < 1.0f) {
                    canvas.restore();
                }
                canvas.restore();
            }
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return false;
        }
    }
}
