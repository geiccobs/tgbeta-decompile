package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TranslateAlert2;
/* loaded from: classes5.dex */
public class TranslateAlert2 extends BottomSheet {
    private TextView allTextsView;
    private ImageView backButton;
    private FrameLayout bulletinContainer;
    private FrameLayout buttonContainerView;
    private FrameLayout buttonShadowView;
    private TextView buttonTextView;
    private FrameLayout buttonView;
    private BaseFragment fragment;
    private String fromLanguage;
    private HeaderView header;
    private FrameLayout headerShadowView;
    private LinearLayoutManager layoutManager;
    private LinkSpanDrawable.LinkCollector links;
    public RecyclerListView listView;
    private boolean noforwards;
    private Runnable onDismiss;
    private OnLinkPress onLinkPress;
    private View paddingView;
    private LinkSpanDrawable pressedLink;
    private ImageView subtitleArrowView;
    private InlineLoadingTextView subtitleFromView;
    private TextView subtitleToView;
    private LinearLayout subtitleView;
    private CharSequence text;
    private ArrayList<CharSequence> textBlocks;
    private TextBlocksLayout textsView;
    private TextView titleView;
    private String toLanguage;
    public static volatile DispatchQueue translateQueue = new DispatchQueue("translateQueue", false);
    private static final int MOST_SPEC = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);
    private int blockIndex = 0;
    private boolean allowScroll = true;
    private android.graphics.Rect containerRect = new android.graphics.Rect();
    private android.graphics.Rect textRect = new android.graphics.Rect();
    private android.graphics.Rect translateMoreRect = new android.graphics.Rect();
    private android.graphics.Rect buttonRect = new android.graphics.Rect();
    private android.graphics.Rect backRect = new android.graphics.Rect();
    private android.graphics.Rect scrollRect = new android.graphics.Rect();
    private float fromY = 0.0f;
    private boolean pressedOutside = false;
    private boolean maybeScrolling = false;
    private boolean scrolling = false;
    private boolean fromScrollRect = false;
    private boolean fromTranslateMoreView = false;
    private float fromScrollViewY = 0.0f;
    private Spannable allTexts = null;
    protected ColorDrawable backDrawable = new ColorDrawable(-16777216) { // from class: org.telegram.ui.Components.TranslateAlert2.10
        @Override // android.graphics.drawable.ColorDrawable, android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            super.setAlpha(alpha);
        }
    };
    private boolean loading = false;
    private boolean loaded = false;

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

    @Override // android.app.Dialog
    public void onBackPressed() {
        dismiss();
    }

    /* loaded from: classes5.dex */
    public class HeaderView extends FrameLayout {
        private float expandedT = 0.0f;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public HeaderView(Context context) {
            super(context);
            TranslateAlert2.this = r1;
        }

        public void setExpandedT(float value) {
            int flags;
            TranslateAlert2.this.backButton.setAlpha(value);
            TranslateAlert2.this.headerShadowView.setAlpha(value);
            if (Math.abs(this.expandedT - value) > 0.01f) {
                if (Build.VERSION.SDK_INT >= 23) {
                    boolean z = true;
                    boolean z2 = this.expandedT > 0.5f;
                    if (value <= 0.5f) {
                        z = false;
                    }
                    if (z2 != z) {
                        int flags2 = TranslateAlert2.this.containerView.getSystemUiVisibility();
                        if (value > 0.5f) {
                            flags = flags2 | 8192;
                        } else {
                            flags = flags2 & (-8193);
                        }
                        TranslateAlert2.this.containerView.setSystemUiVisibility(flags);
                    }
                }
                this.expandedT = value;
                invalidate();
            }
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            TranslateAlert2.this.titleView.layout(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f), (right - left) - AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f) + TranslateAlert2.this.titleView.getMeasuredHeight());
            TranslateAlert2.this.subtitleView.layout(AndroidUtilities.dp(22.0f) - LoadingTextView2.paddingHorizontal, AndroidUtilities.dp(47.0f) - LoadingTextView2.paddingVertical, ((right - left) - AndroidUtilities.dp(22.0f)) - LoadingTextView2.paddingHorizontal, (AndroidUtilities.dp(47.0f) - LoadingTextView2.paddingVertical) + TranslateAlert2.this.subtitleView.getMeasuredHeight());
            TranslateAlert2.this.backButton.layout(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            TranslateAlert2.this.headerShadowView.layout(0, AndroidUtilities.dp(55.0f), right - left, AndroidUtilities.dp(56.0f));
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child != TranslateAlert2.this.titleView) {
                if (child == TranslateAlert2.this.subtitleView) {
                    canvas.save();
                    canvas.translate(AndroidUtilities.dp(this.expandedT * 50.0f), AndroidUtilities.dp(this.expandedT * (-17.0f)));
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return result;
                }
                boolean result2 = super.drawChild(canvas, child, drawingTime);
                return result2;
            }
            canvas.save();
            canvas.translate(AndroidUtilities.dp(this.expandedT * 50.0f), AndroidUtilities.dp(this.expandedT * (-14.0f)));
            float f = this.expandedT;
            canvas.scale(1.0f - (f * 0.111f), 1.0f - (f * 0.111f), child.getX(), child.getY() + (child.getMeasuredHeight() / 2));
            boolean result3 = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return result3;
        }

        @Override // android.view.ViewGroup
        protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
            if (child == TranslateAlert2.this.backButton) {
                TranslateAlert2.this.backButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), C.BUFFER_FLAG_ENCRYPTED));
            } else {
                super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
            }
        }
    }

    public void updateCanExpand() {
        boolean z = true;
        if (!this.listView.canScrollVertically(1) && !this.listView.canScrollVertically(-1)) {
            z = false;
        }
        boolean canExpand = z;
        float canExpandAlpha = canExpand ? 1.0f : 0.0f;
        this.buttonShadowView.animate().alpha(canExpandAlpha).setDuration(200L).start();
        this.allowScroll = canExpand;
    }

    public TranslateAlert2(BaseFragment fragment, Context context, String fromLanguage, String toLanguage, CharSequence text, boolean noforwards, OnLinkPress onLinkPress, Runnable onDismiss) {
        super(context, false);
        fixNavigationBar();
        this.onLinkPress = onLinkPress;
        this.noforwards = noforwards;
        this.fragment = fragment;
        this.fromLanguage = (fromLanguage == null || !fromLanguage.equals("und")) ? fromLanguage : "auto";
        this.toLanguage = toLanguage;
        this.text = text;
        this.textBlocks = cutInBlocks(text, 1024);
        this.onDismiss = onDismiss;
        if (noforwards) {
            getWindow().addFlags(8192);
        }
        this.allTextsView = new TextView(context) { // from class: org.telegram.ui.Components.TranslateAlert2.1
            @Override // android.widget.TextView, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, TranslateAlert2.MOST_SPEC);
            }

            @Override // android.widget.TextView, android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.save();
                canvas.translate(getPaddingLeft(), getPaddingTop());
                if (TranslateAlert2.this.links != null && TranslateAlert2.this.links.draw(canvas)) {
                    invalidate();
                }
                canvas.restore();
            }

            @Override // android.widget.TextView
            public boolean onTextContextMenuItem(int id) {
                if (id == 16908321 && isFocused()) {
                    ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                    ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, getText().subSequence(Math.max(0, Math.min(getSelectionStart(), getSelectionEnd())), Math.max(0, Math.max(getSelectionStart(), getSelectionEnd()))));
                    clipboard.setPrimaryClip(clip);
                    BulletinFactory.of(TranslateAlert2.this.bulletinContainer, null).createCopyBulletin(LocaleController.getString("TextCopied", R.string.TextCopied)).show();
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
        try {
            if (Build.VERSION.SDK_INT >= 29 && !XiaomiUtilities.isMIUI()) {
                int handleColor = Theme.getColor(Theme.key_chat_TextSelectionCursor);
                Drawable left = this.allTextsView.getTextSelectHandleLeft();
                left.setColorFilter(handleColor, PorterDuff.Mode.SRC_IN);
                this.allTextsView.setTextSelectHandleLeft(left);
                Drawable right = this.allTextsView.getTextSelectHandleRight();
                right.setColorFilter(handleColor, PorterDuff.Mode.SRC_IN);
                this.allTextsView.setTextSelectHandleRight(right);
            }
        } catch (Exception e) {
        }
        this.allTextsView.setMovementMethod(new LinkMovementMethod());
        TextBlocksLayout textBlocksLayout = new TextBlocksLayout(context, AndroidUtilities.dp(16.0f), Theme.getColor(Theme.key_dialogTextBlack), this.allTextsView) { // from class: org.telegram.ui.Components.TranslateAlert2.2
            @Override // org.telegram.ui.Components.TranslateAlert2.TextBlocksLayout
            protected void onHeightUpdated(int height, int dy) {
                TranslateAlert2.this.paddingView.requestFocus();
                TranslateAlert2.this.paddingView.requestLayout();
            }
        };
        this.textsView = textBlocksLayout;
        textBlocksLayout.setPadding(AndroidUtilities.dp(22.0f) - LoadingTextView2.paddingHorizontal, AndroidUtilities.dp(20.0f) - LoadingTextView2.paddingVertical, AndroidUtilities.dp(22.0f) - LoadingTextView2.paddingHorizontal, AndroidUtilities.dp(12.0f) - LoadingTextView2.paddingVertical);
        Iterator<CharSequence> it = this.textBlocks.iterator();
        while (it.hasNext()) {
            CharSequence blockText = it.next();
            this.textsView.addBlock(blockText);
        }
        final Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Theme.getColor(Theme.key_dialogBackground));
        backgroundPaint.setShadowLayer(AndroidUtilities.dp(2.0f), 0.0f, AndroidUtilities.dp(-0.66f), 503316480);
        final Paint navigationBarPaint = new Paint();
        navigationBarPaint.setColor(Theme.getColor(Theme.key_dialogBackgroundGray));
        this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.TranslateAlert2.3
            @Override // android.view.View
            public boolean hasOverlappingRendering() {
                return false;
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                float top = TranslateAlert2.this.getCurrentItemTop();
                float expandedT = 1.0f - Math.max(0.0f, Math.min(top / AndroidUtilities.dp(48.0f), 1.0f));
                TranslateAlert2.this.header.setTranslationY(top);
                TranslateAlert2.this.header.setExpandedT(expandedT);
                TranslateAlert2.this.updateCanExpand();
                float r = AndroidUtilities.dp(12.0f) * (1.0f - expandedT);
                AndroidUtilities.rectTmp.set(TranslateAlert2.this.backgroundPaddingLeft, ((getPaddingTop() + top) - ((AndroidUtilities.statusBarHeight + AndroidUtilities.dp(8.0f)) * expandedT)) + AndroidUtilities.dp(8.0f), getWidth() - TranslateAlert2.this.backgroundPaddingLeft, getPaddingTop() + top + AndroidUtilities.dp(20.0f));
                canvas.drawRoundRect(AndroidUtilities.rectTmp, r, r, navigationBarPaint);
                AndroidUtilities.rectTmp.set(TranslateAlert2.this.backgroundPaddingLeft, getPaddingTop() + top, getWidth() - TranslateAlert2.this.backgroundPaddingLeft, getPaddingTop() + getHeight() + AndroidUtilities.dp(12.0f));
                canvas.drawRoundRect(AndroidUtilities.rectTmp, r, r, backgroundPaint);
                super.dispatchDraw(canvas);
            }

            @Override // android.view.View
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                TranslateAlert2.this.onContainerTranslationYChanged(translationY);
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child == TranslateAlert2.this.listView) {
                    canvas.save();
                    canvas.clipRect(0, getPaddingTop() + TranslateAlert2.this.listView.getPaddingTop(), getWidth(), getHeight() - TranslateAlert2.this.listView.getPaddingBottom());
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return result;
                }
                boolean result2 = super.drawChild(canvas, child, drawingTime);
                return result2;
            }
        };
        this.containerView.setPadding(this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, this.backgroundPaddingLeft, 0);
        this.containerView.setClipChildren(false);
        this.containerView.setClipToPadding(false);
        this.containerView.setWillNotDraw(false);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.TranslateAlert2.4
            @Override // android.view.ViewGroup
            public View getFocusedChild() {
                return TranslateAlert2.this.textsView;
            }

            @Override // androidx.recyclerview.widget.RecyclerView
            public void onScrolled(int dx, int dy) {
                TranslateAlert2.this.checkForNextLoading();
                super.onScrolled(dx, dy);
                TranslateAlert2.this.containerView.invalidate();
            }

            @Override // androidx.recyclerview.widget.RecyclerView
            public void onScrollStateChanged(int state) {
                super.onScrollStateChanged(state);
                if (state == 0 && TranslateAlert2.this.header.expandedT > 0.0f && TranslateAlert2.this.header.expandedT < 1.0f) {
                    smoothScrollBy(0, (int) TranslateAlert2.this.header.getTranslationY());
                }
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public boolean onTouchEvent(MotionEvent e2) {
                if (!TranslateAlert2.this.allowScroll) {
                    return false;
                }
                return super.onTouchEvent(e2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent e2) {
                if (!TranslateAlert2.this.allowScroll) {
                    return false;
                }
                return super.onInterceptTouchEvent(e2);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setClipChildren(true);
        this.listView.setClipToPadding(true);
        this.listView.setPadding(0, AndroidUtilities.dp(56.0f), 0, AndroidUtilities.dp(80.0f));
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) { // from class: org.telegram.ui.Components.TranslateAlert2.5
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
            }
        };
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.layoutManager.setOrientation(1);
        this.layoutManager.setReverseLayout(true);
        this.listView.setAdapter(new RecyclerView.Adapter() { // from class: org.telegram.ui.Components.TranslateAlert2.6
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == 0) {
                    return new RecyclerListView.Holder(TranslateAlert2.this.textsView);
                }
                return new RecyclerListView.Holder(TranslateAlert2.this.paddingView);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int position) {
                return position;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return 2;
            }
        });
        View redDot = new View(context) { // from class: org.telegram.ui.Components.TranslateAlert2.7
            @Override // android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), C.BUFFER_FLAG_ENCRYPTED));
            }
        };
        redDot.setBackgroundColor(SupportMenu.CATEGORY_MASK);
        this.containerView.addView(redDot);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        HeaderView headerView = new HeaderView(context);
        this.header = headerView;
        headerView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        this.containerView.addView(this.header, LayoutHelper.createFrame(-1, -2, 55));
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setPivotX(LocaleController.isRTL ? this.titleView.getWidth() : 0.0f);
        this.titleView.setPivotY(0.0f);
        this.titleView.setLines(1);
        this.titleView.setText(LocaleController.getString("AutomaticTranslation", R.string.AutomaticTranslation));
        int i = 5;
        this.titleView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.titleView.setTextSize(1, 18.0f);
        this.header.addView(this.titleView, LayoutHelper.createFrame(-1, -2.0f, 55, 22.0f, 22.0f, 22.0f, 0.0f));
        this.titleView.post(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                TranslateAlert2.this.m3172lambda$new$0$orgtelegramuiComponentsTranslateAlert2();
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
        InlineLoadingTextView inlineLoadingTextView = new InlineLoadingTextView(context, fromLanguageName == null ? languageName(toLanguage) : fromLanguageName, AndroidUtilities.dp(14.0f), Theme.getColor(Theme.key_player_actionBarSubtitle)) { // from class: org.telegram.ui.Components.TranslateAlert2.8
            @Override // org.telegram.ui.Components.TranslateAlert2.InlineLoadingTextView
            protected void onLoadAnimation(float t) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) TranslateAlert2.this.subtitleFromView.getLayoutParams();
                if (lp != null) {
                    if (LocaleController.isRTL) {
                        lp.leftMargin = AndroidUtilities.dp(2.0f - (6.0f * t));
                    } else {
                        lp.rightMargin = AndroidUtilities.dp(2.0f - (6.0f * t));
                    }
                    TranslateAlert2.this.subtitleFromView.setLayoutParams(lp);
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
            this.subtitleView.addView(this.subtitleArrowView, LayoutHelper.createLinear(-2, -2, 16, 3, 1, 3, 0));
            this.subtitleView.addView(this.subtitleFromView, LayoutHelper.createLinear(-2, -2, 16, 0, 0, 0, 0));
        } else {
            this.subtitleView.setPadding(0, 0, InlineLoadingTextView.paddingHorizontal, 0);
            this.subtitleView.addView(this.subtitleFromView, LayoutHelper.createLinear(-2, -2, 16, 0, 0, 0, 0));
            this.subtitleView.addView(this.subtitleArrowView, LayoutHelper.createLinear(-2, -2, 16, 3, 1, 3, 0));
            this.subtitleView.addView(this.subtitleToView, LayoutHelper.createLinear(-2, -2, 16));
        }
        if (fromLanguageName != null) {
            this.subtitleFromView.set(fromLanguageName);
        }
        this.header.addView(this.subtitleView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, 22.0f - (LoadingTextView2.paddingHorizontal / AndroidUtilities.density), 47.0f - (LoadingTextView2.paddingVertical / AndroidUtilities.density), 22.0f - (LoadingTextView2.paddingHorizontal / AndroidUtilities.density), 0.0f));
        ImageView imageView2 = new ImageView(context);
        this.backButton = imageView2;
        imageView2.setImageResource(R.drawable.ic_ab_back);
        this.backButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlack), PorterDuff.Mode.MULTIPLY));
        this.backButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        this.backButton.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        this.backButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector)));
        this.backButton.setClickable(false);
        this.backButton.setAlpha(0.0f);
        this.backButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TranslateAlert2.this.m3173lambda$new$1$orgtelegramuiComponentsTranslateAlert2(view);
            }
        });
        this.header.addView(this.backButton, LayoutHelper.createFrame(56, 56, 3));
        FrameLayout frameLayout = new FrameLayout(context);
        this.headerShadowView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.headerShadowView.setAlpha(0.0f);
        this.header.addView(this.headerShadowView, LayoutHelper.createFrame(-1, 1, 87));
        this.paddingView = new View(context) { // from class: org.telegram.ui.Components.TranslateAlert2.9
            @Override // android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int padding = (int) Math.max(AndroidUtilities.displaySize.y * 0.5f, ((TranslateAlert2.this.listView.getMeasuredHeight() - TranslateAlert2.this.listView.getPaddingTop()) - TranslateAlert2.this.listView.getPaddingBottom()) - TranslateAlert2.this.textsView.height());
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(padding, C.BUFFER_FLAG_ENCRYPTED));
            }
        };
        fetchNext();
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
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.buttonView = frameLayout2;
        frameLayout2.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        this.buttonView.addView(this.buttonTextView);
        this.buttonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TranslateAlert2.this.m3174lambda$new$2$orgtelegramuiComponentsTranslateAlert2(view);
            }
        });
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.buttonContainerView = frameLayout3;
        frameLayout3.addView(this.buttonView, LayoutHelper.createFrame(-1, -1.0f, 119, 16.0f, 16.0f, 16.0f, 16.0f));
        FrameLayout frameLayout4 = new FrameLayout(context);
        this.buttonShadowView = frameLayout4;
        frameLayout4.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.buttonShadowView.setAlpha(0.0f);
        this.buttonContainerView.addView(this.buttonShadowView, LayoutHelper.createFrame(-1, 1, 55));
        this.containerView.addView(this.buttonContainerView, LayoutHelper.createFrame(-1, 80, 87));
        this.bulletinContainer = new FrameLayout(context);
        this.containerView.addView(this.bulletinContainer, LayoutHelper.createFrame(-1, -1.0f, 119, 0.0f, 0.0f, 0.0f, 81.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-TranslateAlert2 */
    public /* synthetic */ void m3172lambda$new$0$orgtelegramuiComponentsTranslateAlert2() {
        this.titleView.setPivotX(LocaleController.isRTL ? this.titleView.getWidth() : 0.0f);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-TranslateAlert2 */
    public /* synthetic */ void m3173lambda$new$1$orgtelegramuiComponentsTranslateAlert2(View e) {
        if (this.backButton.getAlpha() > 0.5f) {
            dismiss();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-TranslateAlert2 */
    public /* synthetic */ void m3174lambda$new$2$orgtelegramuiComponentsTranslateAlert2(View e) {
        dismiss();
    }

    public float getCurrentItemTop() {
        RecyclerListView.Holder holder;
        View child = this.listView.getChildAt(0);
        if (child != null && (holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child)) != null && holder.getAdapterPosition() == 0) {
            return Math.max(0.0f, child.getY() - this.header.getMeasuredHeight());
        }
        return 0.0f;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return !this.listView.canScrollVertically(-1);
    }

    private boolean hasSelection() {
        return this.allTextsView.hasSelection();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.containerView.setPadding(this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, this.backgroundPaddingLeft, 0);
    }

    public String languageName(String locale) {
        LocaleController.LocaleInfo thisLanguageInfo;
        if (locale == null || locale.equals("und") || locale.equals("auto") || (thisLanguageInfo = LocaleController.getInstance().getBuiltinLanguageByPlural(locale)) == null) {
            return null;
        }
        LocaleController.LocaleInfo currentLanguageInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        boolean isCurrentLanguageEnglish = currentLanguageInfo != null && "en".equals(currentLanguageInfo.pluralLangCode);
        if (isCurrentLanguageEnglish) {
            return thisLanguageInfo.nameEnglish;
        }
        return thisLanguageInfo.name;
    }

    public void updateSourceLanguage() {
        String fromLanguageName = languageName(this.fromLanguage);
        if (fromLanguageName != null) {
            this.subtitleView.setAlpha(1.0f);
            if (!this.subtitleFromView.loaded) {
                this.subtitleFromView.loaded(fromLanguageName);
            }
        } else if (this.loaded) {
            this.subtitleView.animate().alpha(0.0f).setDuration(150L).start();
            this.titleView.animate().scaleX(1.2f).scaleY(1.2f).translationY(AndroidUtilities.dp(5.0f)).setDuration(150L).start();
        }
    }

    private ArrayList<CharSequence> cutInBlocks(CharSequence full, int maxBlockSize) {
        ArrayList<CharSequence> blocks = new ArrayList<>();
        if (full == null) {
            return blocks;
        }
        while (full.length() > maxBlockSize) {
            String maxBlockStr = full.subSequence(0, maxBlockSize).toString();
            int n = maxBlockStr.lastIndexOf("\n\n");
            if (n == -1) {
                n = maxBlockStr.lastIndexOf("\n");
            }
            if (n == -1) {
                n = maxBlockStr.lastIndexOf(". ");
            }
            if (n == -1) {
                n = maxBlockStr.length();
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
        fetchTranslation(this.textBlocks.get(this.blockIndex), Math.min((this.blockIndex + 1) * 1000, 3500), new OnTranslationSuccess() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.TranslateAlert2.OnTranslationSuccess
            public final void run(String str, String str2) {
                TranslateAlert2.this.m3169lambda$fetchNext$4$orgtelegramuiComponentsTranslateAlert2(str, str2);
            }
        }, new OnTranslationFail() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.TranslateAlert2.OnTranslationFail
            public final void run(boolean z) {
                TranslateAlert2.this.m3170lambda$fetchNext$5$orgtelegramuiComponentsTranslateAlert2(z);
            }
        });
        return true;
    }

    /* renamed from: lambda$fetchNext$4$org-telegram-ui-Components-TranslateAlert2 */
    public /* synthetic */ void m3169lambda$fetchNext$4$orgtelegramuiComponentsTranslateAlert2(String translatedText, String sourceLanguage) {
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
                    spannable.setSpan(new ClickableSpan() { // from class: org.telegram.ui.Components.TranslateAlert2.11
                        @Override // android.text.style.ClickableSpan
                        public void onClick(View view) {
                            if (TranslateAlert2.this.onLinkPress != null) {
                                TranslateAlert2.this.onLinkPress.run(urlSpan);
                                TranslateAlert2.this.dismiss();
                                return;
                            }
                            AlertsCreator.showOpenUrlAlert(TranslateAlert2.this.fragment, urlSpan.getURL(), false, false);
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
                    spannable.setSpan(new ClickableSpan() { // from class: org.telegram.ui.Components.TranslateAlert2.12
                        @Override // android.text.style.ClickableSpan
                        public void onClick(View view) {
                            AlertsCreator.showOpenUrlAlert(TranslateAlert2.this.fragment, urlSpan2.getURL(), false, false);
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
            block.loaded(spannable, new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    TranslateAlert2.this.m3168lambda$fetchNext$3$orgtelegramuiComponentsTranslateAlert2();
                }
            });
        }
        if (sourceLanguage != null) {
            this.fromLanguage = sourceLanguage;
            updateSourceLanguage();
        }
        this.blockIndex++;
        this.loading = false;
    }

    /* renamed from: lambda$fetchNext$3$org-telegram-ui-Components-TranslateAlert2 */
    public /* synthetic */ void m3168lambda$fetchNext$3$orgtelegramuiComponentsTranslateAlert2() {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                TranslateAlert2.this.checkForNextLoading();
            }
        });
    }

    /* renamed from: lambda$fetchNext$5$org-telegram-ui-Components-TranslateAlert2 */
    public /* synthetic */ void m3170lambda$fetchNext$5$orgtelegramuiComponentsTranslateAlert2(boolean rateLimit) {
        String str;
        Context context = getContext();
        if (rateLimit) {
            str = LocaleController.getString("TranslationFailedAlert1", R.string.TranslationFailedAlert1);
        } else {
            str = LocaleController.getString("TranslationFailedAlert2", R.string.TranslationFailedAlert2);
        }
        Toast.makeText(context, str, 0).show();
        if (this.blockIndex == 0) {
            dismiss();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        super.dismissInternal();
        Runnable runnable = this.onDismiss;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void checkForNextLoading() {
        if (!this.listView.canScrollVertically(-1)) {
            fetchNext();
        }
    }

    private void fetchTranslation(final CharSequence text, final long minDuration, final OnTranslationSuccess onSuccess, final OnTranslationFail onFail) {
        if (!translateQueue.isAlive()) {
            translateQueue.start();
        }
        translateQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                TranslateAlert2.this.m3171x3c102fbe(text, onSuccess, minDuration, onFail);
            }
        });
    }

    /* JADX WARN: Not initialized variable reg: 20, insn: 0x0175: MOVE  (r4 I:??[OBJECT, ARRAY]) = (r20 I:??[OBJECT, ARRAY] A[D('uri' java.lang.String)]), block:B:51:0x0175 */
    /* renamed from: lambda$fetchTranslation$9$org-telegram-ui-Components-TranslateAlert2 */
    public /* synthetic */ void m3171x3c102fbe(CharSequence text, final OnTranslationSuccess onSuccess, long minDuration, final OnTranslationFail onFail) {
        Exception e;
        boolean z;
        Throwable th;
        HttpURLConnection connection = null;
        long start = SystemClock.elapsedRealtime();
        int i = 0;
        try {
            String uri = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + Uri.encode(this.fromLanguage);
            try {
                connection = (HttpURLConnection) new URI((((uri + "&tl=") + Uri.encode(this.toLanguage)) + "&dt=t&ie=UTF-8&oe=UTF-8&otf=1&ssel=0&tsel=0&kc=7&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&q=") + Uri.encode(text.toString())).toURL().openConnection();
                connection.setRequestMethod(DefaultHttpClient.METHOD_GET);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
                connection.setRequestProperty(DefaultHttpClient.CONTENT_TYPE_KEY, "application/json");
                StringBuilder textBuilder = new StringBuilder();
                Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
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
                        Exception e3 = e;
                        try {
                            StringBuilder sb = new StringBuilder();
                            sb.append("failed to translate a text ");
                            String str = null;
                            sb.append(connection != null ? Integer.valueOf(connection.getResponseCode()) : null);
                            sb.append(" ");
                            if (connection != null) {
                                str = connection.getResponseMessage();
                            }
                            sb.append(str);
                            Log.e("translate", sb.toString());
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        e3.printStackTrace();
                        if (onFail != null) {
                            if (connection != null) {
                                try {
                                    if (connection.getResponseCode() == 429) {
                                        z = true;
                                        final boolean rateLimit = z;
                                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda5
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                TranslateAlert2.OnTranslationFail.this.run(rateLimit);
                                            }
                                        });
                                        return;
                                    }
                                } catch (Exception e4) {
                                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda4
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            TranslateAlert2.OnTranslationFail.this.run(false);
                                        }
                                    });
                                    return;
                                }
                            }
                            z = false;
                            final boolean rateLimit2 = z;
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda5
                                @Override // java.lang.Runnable
                                public final void run() {
                                    TranslateAlert2.OnTranslationFail.this.run(rateLimit2);
                                }
                            });
                            return;
                        }
                        return;
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
                } catch (Exception e5) {
                }
                if (sourceLanguage != null && sourceLanguage.contains("-")) {
                    sourceLanguage = sourceLanguage.substring(0, sourceLanguage.indexOf("-"));
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
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda6
                        @Override // java.lang.Runnable
                        public final void run() {
                            TranslateAlert2.lambda$fetchTranslation$6(TranslateAlert2.OnTranslationSuccess.this, finalResult, finalSourceLanguage);
                        }
                    }, Math.max(0L, minDuration - elapsed));
                }
                final String finalResult2 = result.toString();
                final String finalSourceLanguage2 = sourceLanguage;
                long elapsed2 = SystemClock.elapsedRealtime() - start;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        TranslateAlert2.lambda$fetchTranslation$6(TranslateAlert2.OnTranslationSuccess.this, finalResult2, finalSourceLanguage2);
                    }
                }, Math.max(0L, minDuration - elapsed2));
            } catch (Exception e6) {
                e = e6;
            }
        } catch (Exception e7) {
            e = e7;
        }
    }

    public static /* synthetic */ void lambda$fetchTranslation$6(OnTranslationSuccess onSuccess, String finalResult, String finalSourceLanguage) {
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
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, TranslateAlert2$$ExternalSyntheticLambda11.INSTANCE);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ void lambda$translateText$10(TLObject error, TLRPC.TL_error res) {
    }

    public static TranslateAlert2 showAlert(Context context, BaseFragment fragment, int currentAccount, TLRPC.InputPeer peer, int msgId, String fromLanguage, String toLanguage, CharSequence text, boolean noforwards, OnLinkPress onLinkPress, Runnable onDismiss) {
        if (peer != null) {
            translateText(currentAccount, peer, msgId, (fromLanguage == null || !fromLanguage.equals("und")) ? fromLanguage : null, toLanguage);
        }
        TranslateAlert2 alert = new TranslateAlert2(fragment, context, fromLanguage, toLanguage, text, noforwards, onLinkPress, onDismiss);
        if (fragment != null) {
            if (fragment.getParentActivity() != null) {
                fragment.showDialog(alert);
            }
        } else {
            alert.show();
        }
        return alert;
    }

    public static TranslateAlert2 showAlert(Context context, BaseFragment fragment, String fromLanguage, String toLanguage, CharSequence text, boolean noforwards, OnLinkPress onLinkPress, Runnable onDismiss) {
        TranslateAlert2 alert = new TranslateAlert2(fragment, context, fromLanguage, toLanguage, text, noforwards, onLinkPress, onDismiss);
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

        protected void onHeightUpdated(int height, int dy) {
        }

        public void updateHeight() {
            boolean updated;
            int newHeight = height();
            int dy = 0;
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) getLayoutParams();
            if (lp == null) {
                lp = new RecyclerView.LayoutParams(-1, newHeight);
                updated = true;
            } else {
                updated = lp.height != newHeight;
                dy = newHeight - lp.height;
                lp.height = newHeight;
            }
            if (updated) {
                setLayoutParams(lp);
                onHeightUpdated(newHeight, dy);
            }
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int count = getBlocksCount();
            int innerWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()) - getPaddingRight(), View.MeasureSpec.getMode(widthMeasureSpec));
            for (int i = 0; i < count; i++) {
                LoadingTextView2 block = getBlockAt(i);
                block.measure(innerWidthMeasureSpec, TranslateAlert2.MOST_SPEC);
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
        public static final int paddingHorizontal = AndroidUtilities.dp(4.0f);
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
            TextView textView = new TextView(context) { // from class: org.telegram.ui.Components.TranslateAlert2.InlineLoadingTextView.1
                @Override // android.widget.TextView, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(TranslateAlert2.MOST_SPEC, TranslateAlert2.MOST_SPEC);
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
            addView(textView);
            TextView textView2 = new TextView(context) { // from class: org.telegram.ui.Components.TranslateAlert2.InlineLoadingTextView.2
                @Override // android.widget.TextView, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(TranslateAlert2.MOST_SPEC, TranslateAlert2.MOST_SPEC);
                }
            };
            this.toTextView = textView2;
            textView2.setTextSize(0, fontSize);
            textView2.setTextColor(textColor);
            textView2.setLines(1);
            textView2.setMaxLines(1);
            textView2.setSingleLine(true);
            textView2.setEllipsize(null);
            addView(textView2);
            int c1 = Theme.getColor(Theme.key_dialogBackground);
            int c2 = Theme.getColor(Theme.key_dialogBackgroundGray);
            LinearGradient gradient = new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{c1, c2, c1}, new float[]{0.0f, 0.67f, 1.0f}, Shader.TileMode.REPEAT);
            paint.setShader(gradient);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.loadingAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TranslateAlert2$InlineLoadingTextView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TranslateAlert2.InlineLoadingTextView.this.m3176xe0d11689(valueAnimator);
                }
            });
            ofFloat.setDuration(Long.MAX_VALUE);
            ofFloat.start();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-TranslateAlert2$InlineLoadingTextView */
        public /* synthetic */ void m3176xe0d11689(ValueAnimator a) {
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
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TranslateAlert2$InlineLoadingTextView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        TranslateAlert2.InlineLoadingTextView.this.m3175xd95ca609(valueAnimator);
                    }
                });
                this.loadedAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TranslateAlert2.InlineLoadingTextView.3
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

        /* renamed from: lambda$loaded$1$org-telegram-ui-Components-TranslateAlert2$InlineLoadingTextView */
        public /* synthetic */ void m3175xd95ca609(ValueAnimator a) {
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
        public static final int paddingHorizontal = AndroidUtilities.dp(4.0f);
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
            TextView textView = new TextView(context) { // from class: org.telegram.ui.Components.TranslateAlert2.LoadingTextView2.1
                @Override // android.widget.TextView, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, TranslateAlert2.MOST_SPEC);
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
            addView(textView);
            TextView textView2 = new TextView(context) { // from class: org.telegram.ui.Components.TranslateAlert2.LoadingTextView2.2
                @Override // android.widget.TextView, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, TranslateAlert2.MOST_SPEC);
                }
            };
            this.toTextView = textView2;
            textView2.setTextSize(0, fontSize);
            textView2.setTextColor(textColor);
            textView2.setLines(0);
            textView2.setMaxLines(0);
            textView2.setSingleLine(false);
            textView2.setEllipsize(null);
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
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TranslateAlert2$LoadingTextView2$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TranslateAlert2.LoadingTextView2.this.m3178xd663c710(scaleFromZero, valueAnimator);
                }
            });
            ofFloat.setDuration(Long.MAX_VALUE);
            ofFloat.start();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-TranslateAlert2$LoadingTextView2 */
        public /* synthetic */ void m3178xd663c710(boolean scaleFromZero, ValueAnimator a) {
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
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TranslateAlert2$LoadingTextView2$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        TranslateAlert2.LoadingTextView2.this.m3177xb1bc8790(valueAnimator);
                    }
                });
                this.loadedAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TranslateAlert2.LoadingTextView2.3
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

        /* renamed from: lambda$loaded$1$org-telegram-ui-Components-TranslateAlert2$LoadingTextView2 */
        public /* synthetic */ void m3177xb1bc8790(ValueAnimator a) {
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
            view.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), TranslateAlert2.MOST_SPEC);
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
