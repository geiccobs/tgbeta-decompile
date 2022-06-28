package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AboutLinkCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.URLSpanNoUnderline;
/* loaded from: classes4.dex */
public class AboutLinkCell extends FrameLayout {
    private static final int COLLAPSED_HEIGHT;
    private static final int MAX_OPEN_HEIGHT;
    private static final int MOST_SPEC = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);
    final float SPACE;
    private Paint backgroundPaint;
    private FrameLayout bottomShadow;
    private ValueAnimator collapseAnimator;
    private FrameLayout container;
    private float expandT;
    private boolean expanded;
    private StaticLayout firstThreeLinesLayout;
    private int lastInlineLine;
    private int lastMaxWidth;
    private LinkSpanDrawable.LinkCollector links;
    Runnable longPressedRunnable;
    private boolean needSpace;
    private StaticLayout[] nextLinesLayouts;
    private Point[] nextLinesLayoutsPositions;
    private String oldText;
    private BaseFragment parentFragment;
    private LinkSpanDrawable pressedLink;
    private float rawCollapseT;
    private Theme.ResourcesProvider resourcesProvider;
    private Drawable rippleBackground;
    private boolean shouldExpand;
    private Drawable showMoreBackgroundDrawable;
    private FrameLayout showMoreTextBackgroundView;
    private TextView showMoreTextView;
    private SpannableStringBuilder stringBuilder;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private LinkPath urlPath;
    private Point urlPathOffset;
    private TextView valueTextView;

    public AboutLinkCell(Context context, BaseFragment fragment) {
        this(context, fragment, null);
    }

    public AboutLinkCell(Context context, BaseFragment fragment, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.urlPathOffset = new Point();
        this.urlPath = new LinkPath(true);
        this.backgroundPaint = new Paint();
        this.SPACE = AndroidUtilities.dp(3.0f);
        this.longPressedRunnable = new AnonymousClass3();
        this.expandT = 0.0f;
        this.rawCollapseT = 0.0f;
        this.expanded = false;
        this.lastMaxWidth = 0;
        this.nextLinesLayouts = null;
        this.lastInlineLine = -1;
        this.needSpace = false;
        this.shouldExpand = false;
        this.resourcesProvider = resourcesProvider;
        this.parentFragment = fragment;
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Cells.AboutLinkCell.1
            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                boolean result = false;
                if (AboutLinkCell.this.textLayout != null || AboutLinkCell.this.nextLinesLayouts != null) {
                    if (event.getAction() == 0 || (AboutLinkCell.this.pressedLink != null && event.getAction() == 1)) {
                        if (x >= AboutLinkCell.this.showMoreTextView.getLeft() && x <= AboutLinkCell.this.showMoreTextView.getRight() && y >= AboutLinkCell.this.showMoreTextView.getTop() && y <= AboutLinkCell.this.showMoreTextView.getBottom()) {
                            return super.onTouchEvent(event);
                        }
                        if (getMeasuredWidth() > 0 && x > getMeasuredWidth() - AndroidUtilities.dp(23.0f)) {
                            return super.onTouchEvent(event);
                        }
                        if (event.getAction() == 0) {
                            if (AboutLinkCell.this.firstThreeLinesLayout != null && AboutLinkCell.this.expandT < 1.0f && AboutLinkCell.this.shouldExpand) {
                                AboutLinkCell aboutLinkCell = AboutLinkCell.this;
                                if (!aboutLinkCell.checkTouchTextLayout(aboutLinkCell.firstThreeLinesLayout, AboutLinkCell.this.textX, AboutLinkCell.this.textY, x, y)) {
                                    if (AboutLinkCell.this.nextLinesLayouts != null) {
                                        int i = 0;
                                        while (true) {
                                            if (i >= AboutLinkCell.this.nextLinesLayouts.length) {
                                                break;
                                            }
                                            AboutLinkCell aboutLinkCell2 = AboutLinkCell.this;
                                            if (!aboutLinkCell2.checkTouchTextLayout(aboutLinkCell2.nextLinesLayouts[i], AboutLinkCell.this.nextLinesLayoutsPositions[i].x, AboutLinkCell.this.nextLinesLayoutsPositions[i].y, x, y)) {
                                                i++;
                                            } else {
                                                result = true;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    result = true;
                                }
                            } else {
                                AboutLinkCell aboutLinkCell3 = AboutLinkCell.this;
                                if (aboutLinkCell3.checkTouchTextLayout(aboutLinkCell3.textLayout, AboutLinkCell.this.textX, AboutLinkCell.this.textY, x, y)) {
                                    result = true;
                                }
                            }
                            if (!result) {
                                AboutLinkCell.this.resetPressedLink();
                            }
                        } else if (AboutLinkCell.this.pressedLink != null) {
                            try {
                                AboutLinkCell aboutLinkCell4 = AboutLinkCell.this;
                                aboutLinkCell4.onLinkClick((ClickableSpan) aboutLinkCell4.pressedLink.getSpan());
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            AboutLinkCell.this.resetPressedLink();
                            result = true;
                        }
                    } else if (event.getAction() == 3) {
                        AboutLinkCell.this.resetPressedLink();
                    }
                }
                return result || super.onTouchEvent(event);
            }
        };
        this.container = frameLayout;
        frameLayout.setImportantForAccessibility(2);
        this.links = new LinkSpanDrawable.LinkCollector(this.container);
        this.container.setClickable(true);
        this.rippleBackground = Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 0, 0);
        TextView textView = new TextView(context);
        this.valueTextView = textView;
        textView.setVisibility(8);
        this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        int i = 5;
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setImportantForAccessibility(2);
        this.valueTextView.setFocusable(false);
        this.container.addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 80, 23.0f, 0.0f, 23.0f, 10.0f));
        this.bottomShadow = new FrameLayout(context);
        Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.gradient_bottom).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider), PorterDuff.Mode.SRC_ATOP));
        this.bottomShadow.setBackground(shadowDrawable);
        addView(this.bottomShadow, LayoutHelper.createFrame(-1, 12.0f, 87, 0.0f, 0.0f, 0.0f, 0.0f));
        addView(this.container, LayoutHelper.createFrame(-1, -1, 55));
        TextView textView2 = new TextView(context) { // from class: org.telegram.ui.Cells.AboutLinkCell.2
            private boolean pressed = false;

            @Override // android.widget.TextView, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                boolean wasPressed = this.pressed;
                if (event.getAction() == 0) {
                    this.pressed = true;
                } else if (event.getAction() != 2) {
                    this.pressed = false;
                }
                if (wasPressed != this.pressed) {
                    invalidate();
                }
                return super.onTouchEvent(event);
            }

            @Override // android.widget.TextView, android.view.View
            protected void onDraw(Canvas canvas) {
                if (this.pressed) {
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, getWidth(), getHeight());
                    canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Theme.chat_urlPaint);
                }
                super.onDraw(canvas);
            }
        };
        this.showMoreTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, resourcesProvider));
        this.showMoreTextView.setTextSize(1, 16.0f);
        this.showMoreTextView.setLines(1);
        this.showMoreTextView.setMaxLines(1);
        this.showMoreTextView.setSingleLine(true);
        this.showMoreTextView.setText(LocaleController.getString("DescriptionMore", R.string.DescriptionMore));
        this.showMoreTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.AboutLinkCell$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AboutLinkCell.this.m1627lambda$new$0$orgtelegramuiCellsAboutLinkCell(view);
            }
        });
        this.showMoreTextView.setPadding(AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(2.0f), 0);
        this.showMoreTextBackgroundView = new FrameLayout(context);
        Drawable mutate = context.getResources().getDrawable(R.drawable.gradient_left).mutate();
        this.showMoreBackgroundDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider), PorterDuff.Mode.MULTIPLY));
        this.showMoreTextBackgroundView.setBackground(this.showMoreBackgroundDrawable);
        FrameLayout frameLayout2 = this.showMoreTextBackgroundView;
        frameLayout2.setPadding(frameLayout2.getPaddingLeft() + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(1.0f), 0, AndroidUtilities.dp(3.0f));
        this.showMoreTextBackgroundView.addView(this.showMoreTextView, LayoutHelper.createFrame(-2, -2.0f));
        FrameLayout frameLayout3 = this.showMoreTextBackgroundView;
        addView(frameLayout3, LayoutHelper.createFrame(-2, -2.0f, 85, 22.0f - (frameLayout3.getPaddingLeft() / AndroidUtilities.density), 0.0f, 22.0f - (this.showMoreTextBackgroundView.getPaddingRight() / AndroidUtilities.density), 6.0f));
        this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider));
        setWillNotDraw(false);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-AboutLinkCell */
    public /* synthetic */ void m1627lambda$new$0$orgtelegramuiCellsAboutLinkCell(View e) {
        updateCollapse(true, true);
    }

    private void setShowMoreMarginBottom(int marginBottom) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this.showMoreTextBackgroundView.getLayoutParams();
        if (lp.bottomMargin != marginBottom) {
            lp.bottomMargin = marginBottom;
            this.showMoreTextBackgroundView.setLayoutParams(lp);
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return false;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        View parent = (View) getParent();
        float alpha = parent == null ? 1.0f : (float) Math.pow(parent.getAlpha(), 2.0d);
        drawText(canvas);
        float viewAlpha = this.showMoreTextBackgroundView.getAlpha();
        if (viewAlpha > 0.0f) {
            canvas.save();
            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (viewAlpha * 255.0f), 31);
            this.showMoreBackgroundDrawable.setAlpha((int) (alpha * 255.0f));
            canvas.translate(this.showMoreTextBackgroundView.getLeft(), this.showMoreTextBackgroundView.getTop());
            this.showMoreTextBackgroundView.draw(canvas);
            canvas.restore();
        }
        float viewAlpha2 = this.bottomShadow.getAlpha();
        if (viewAlpha2 > 0.0f) {
            canvas.save();
            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (255.0f * viewAlpha2), 31);
            canvas.translate(this.bottomShadow.getLeft(), this.bottomShadow.getTop());
            this.bottomShadow.draw(canvas);
            canvas.restore();
        }
        this.container.draw(canvas);
        super.draw(canvas);
    }

    private void drawText(Canvas canvas) {
        StaticLayout staticLayout;
        int line;
        StaticLayout layout;
        int c;
        canvas.save();
        canvas.clipRect(AndroidUtilities.dp(15.0f), AndroidUtilities.dp(8.0f), getWidth() - AndroidUtilities.dp(23.0f), getHeight());
        int dp = AndroidUtilities.dp(23.0f);
        this.textX = dp;
        int line2 = 0;
        canvas.translate(dp, 0.0f);
        LinkSpanDrawable.LinkCollector linkCollector = this.links;
        if (linkCollector != null && linkCollector.draw(canvas)) {
            invalidate();
        }
        int dp2 = AndroidUtilities.dp(8.0f);
        this.textY = dp2;
        canvas.translate(0.0f, dp2);
        try {
            staticLayout = this.firstThreeLinesLayout;
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (staticLayout != null && this.shouldExpand) {
            staticLayout.draw(canvas);
            int lastLine = this.firstThreeLinesLayout.getLineCount() - 1;
            float top = this.firstThreeLinesLayout.getLineTop(lastLine) + this.firstThreeLinesLayout.getTopPadding();
            float x = this.firstThreeLinesLayout.getLineRight(lastLine) + (this.needSpace ? this.SPACE : 0.0f);
            float y = (this.firstThreeLinesLayout.getLineBottom(lastLine) - this.firstThreeLinesLayout.getLineTop(lastLine)) - this.firstThreeLinesLayout.getBottomPadding();
            float t = easeInOutCubic(1.0f - ((float) Math.pow(this.expandT, 0.25d)));
            if (this.nextLinesLayouts != null) {
                float x2 = x;
                float y2 = y;
                int line3 = 0;
                while (true) {
                    StaticLayout[] staticLayoutArr = this.nextLinesLayouts;
                    if (line3 >= staticLayoutArr.length) {
                        break;
                    }
                    StaticLayout layout2 = staticLayoutArr[line3];
                    if (layout2 != null) {
                        int c2 = canvas.save();
                        Point[] pointArr = this.nextLinesLayoutsPositions;
                        if (pointArr[line3] != null) {
                            pointArr[line3].set((int) (this.textX + (x2 * t)), (int) (this.textY + top + ((1.0f - t) * y2)));
                        }
                        int i = this.lastInlineLine;
                        if (i == -1 || i > line3) {
                            c = c2;
                            layout = layout2;
                            line = line3;
                            canvas.translate(x2 * t, ((1.0f - t) * y2) + top);
                        } else {
                            canvas.translate(line2, top + y2);
                            int line4 = (int) (this.expandT * 255.0f);
                            c = c2;
                            layout = layout2;
                            line = line3;
                            canvas.saveLayerAlpha(0.0f, 0.0f, layout2.getWidth(), layout2.getHeight(), line4, 31);
                        }
                        StaticLayout layout3 = layout;
                        layout3.draw(canvas);
                        canvas.restoreToCount(c);
                        x2 += layout3.getLineRight(0) + this.SPACE;
                        y2 += layout3.getLineBottom(0) + layout3.getTopPadding();
                    } else {
                        line = line3;
                    }
                    line3 = line + 1;
                    line2 = 0;
                }
            }
            canvas.restore();
        }
        StaticLayout staticLayout2 = this.textLayout;
        if (staticLayout2 != null) {
            staticLayout2.draw(canvas);
        }
        canvas.restore();
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener l) {
        this.container.setOnClickListener(l);
    }

    protected void didPressUrl(String url) {
    }

    protected void didResizeStart() {
    }

    protected void didResizeEnd() {
    }

    protected void didExtend() {
    }

    public void resetPressedLink() {
        this.links.clear();
        this.pressedLink = null;
        AndroidUtilities.cancelRunOnUIThread(this.longPressedRunnable);
        invalidate();
    }

    public void setText(String text, boolean parseLinks) {
        setTextAndValue(text, null, parseLinks);
    }

    public void setTextAndValue(String text, String value, boolean parseLinks) {
        if (TextUtils.isEmpty(text) || TextUtils.equals(text, this.oldText)) {
            return;
        }
        try {
            this.oldText = AndroidUtilities.getSafeString(text);
        } catch (Throwable th) {
            this.oldText = text;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.oldText);
        this.stringBuilder = spannableStringBuilder;
        MessageObject.addLinks(false, spannableStringBuilder, false, false, !parseLinks);
        Emoji.replaceEmoji(this.stringBuilder, Theme.profile_aboutTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        if (this.lastMaxWidth <= 0) {
            this.lastMaxWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(46.0f);
        }
        checkTextLayout(this.lastMaxWidth, true);
        updateHeight();
        int wasValueVisibility = this.valueTextView.getVisibility();
        if (TextUtils.isEmpty(value)) {
            this.valueTextView.setVisibility(8);
        } else {
            this.valueTextView.setText(value);
            this.valueTextView.setVisibility(0);
        }
        if (wasValueVisibility != this.valueTextView.getVisibility()) {
            checkTextLayout(this.lastMaxWidth, true);
        }
        requestLayout();
    }

    /* renamed from: org.telegram.ui.Cells.AboutLinkCell$3 */
    /* loaded from: classes4.dex */
    public class AnonymousClass3 implements Runnable {
        AnonymousClass3() {
            AboutLinkCell.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AboutLinkCell.this.pressedLink != null) {
                final String url = AboutLinkCell.this.pressedLink.getSpan() instanceof URLSpanNoUnderline ? ((URLSpanNoUnderline) AboutLinkCell.this.pressedLink.getSpan()).getURL() : AboutLinkCell.this.pressedLink.getSpan() instanceof URLSpan ? ((URLSpan) AboutLinkCell.this.pressedLink.getSpan()).getURL() : AboutLinkCell.this.pressedLink.getSpan().toString();
                try {
                    AboutLinkCell.this.performHapticFeedback(0, 2);
                } catch (Exception e) {
                }
                final ClickableSpan pressedLinkFinal = (ClickableSpan) AboutLinkCell.this.pressedLink.getSpan();
                BottomSheet.Builder builder = new BottomSheet.Builder(AboutLinkCell.this.parentFragment.getParentActivity());
                builder.setTitle(url);
                builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Cells.AboutLinkCell$3$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AboutLinkCell.AnonymousClass3.this.m1629lambda$run$0$orgtelegramuiCellsAboutLinkCell$3(pressedLinkFinal, url, dialogInterface, i);
                    }
                });
                builder.setOnPreDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Cells.AboutLinkCell$3$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        AboutLinkCell.AnonymousClass3.this.m1630lambda$run$1$orgtelegramuiCellsAboutLinkCell$3(dialogInterface);
                    }
                });
                builder.show();
                AboutLinkCell.this.pressedLink = null;
            }
        }

        /* renamed from: lambda$run$0$org-telegram-ui-Cells-AboutLinkCell$3 */
        public /* synthetic */ void m1629lambda$run$0$orgtelegramuiCellsAboutLinkCell$3(ClickableSpan pressedLinkFinal, String url, DialogInterface dialog, int which) {
            if (which == 0) {
                AboutLinkCell.this.onLinkClick(pressedLinkFinal);
            } else if (which == 1) {
                AndroidUtilities.addToClipboard(url);
                if (Build.VERSION.SDK_INT < 31) {
                    if (url.startsWith("@")) {
                        BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(R.raw.copy, LocaleController.getString("UsernameCopied", R.string.UsernameCopied)).show();
                    } else if (url.startsWith("#") || url.startsWith("$")) {
                        BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(R.raw.copy, LocaleController.getString("HashtagCopied", R.string.HashtagCopied)).show();
                    } else {
                        BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(R.raw.copy, LocaleController.getString("LinkCopied", R.string.LinkCopied)).show();
                    }
                }
            }
        }

        /* renamed from: lambda$run$1$org-telegram-ui-Cells-AboutLinkCell$3 */
        public /* synthetic */ void m1630lambda$run$1$orgtelegramuiCellsAboutLinkCell$3(DialogInterface di) {
            AboutLinkCell.this.resetPressedLink();
        }
    }

    public boolean checkTouchTextLayout(StaticLayout textLayout, int textX, int textY, int ex, int ey) {
        int x = ex - textX;
        int y = ey - textY;
        try {
            int line = textLayout.getLineForVertical(y);
            int off = textLayout.getOffsetForHorizontal(line, x);
            float left = textLayout.getLineLeft(line);
            if (left <= x && textLayout.getLineWidth(line) + left >= x && y >= 0 && y <= textLayout.getHeight()) {
                Spannable buffer = (Spannable) textLayout.getText();
                ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                if (link.length != 0 && !AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
                    resetPressedLink();
                    LinkSpanDrawable linkSpanDrawable = new LinkSpanDrawable(link[0], this.parentFragment.getResourceProvider(), ex, ey);
                    this.pressedLink = linkSpanDrawable;
                    this.links.addLink(linkSpanDrawable);
                    int start = buffer.getSpanStart(this.pressedLink.getSpan());
                    int end = buffer.getSpanEnd(this.pressedLink.getSpan());
                    LinkPath path = this.pressedLink.obtainNewPath();
                    path.setCurrentLayout(textLayout, start, textY);
                    textLayout.getSelectionPath(start, end, path);
                    AndroidUtilities.runOnUIThread(this.longPressedRunnable, ViewConfiguration.getLongPressTimeout());
                    return true;
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public void onLinkClick(ClickableSpan pressedLink) {
        if (pressedLink instanceof URLSpanNoUnderline) {
            String url = ((URLSpanNoUnderline) pressedLink).getURL();
            if (url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) {
                didPressUrl(url);
            }
        } else if (pressedLink instanceof URLSpan) {
            String url2 = ((URLSpan) pressedLink).getURL();
            if (AndroidUtilities.shouldShowUrlInAlert(url2)) {
                AlertsCreator.showOpenUrlAlert(this.parentFragment, url2, true, true);
            } else {
                Browser.openUrl(getContext(), url2);
            }
        } else {
            pressedLink.onClick(this);
        }
    }

    static {
        int dp = AndroidUtilities.dp(76.0f);
        COLLAPSED_HEIGHT = dp;
        MAX_OPEN_HEIGHT = dp;
    }

    /* loaded from: classes4.dex */
    public class SpringInterpolator {
        public float friction;
        public float tension;
        private final float mass = 1.0f;
        private float position = 0.0f;
        private float velocity = 0.0f;

        public SpringInterpolator(float tension, float friction) {
            AboutLinkCell.this = this$0;
            this.tension = tension;
            this.friction = friction;
        }

        public float getValue(float deltaTime) {
            float deltaTime2 = Math.min(deltaTime, 250.0f);
            while (deltaTime2 > 0.0f) {
                float step = Math.min(deltaTime2, 18.0f);
                step(step);
                deltaTime2 -= step;
            }
            return this.position;
        }

        private void step(float delta) {
            float f = this.position;
            float f2 = this.velocity;
            float acceleration = ((((-this.tension) * 1.0E-6f) * (f - 1.0f)) + (((-this.friction) * 0.001f) * f2)) / 1.0f;
            float f3 = f2 + (acceleration * delta);
            this.velocity = f3;
            this.position = f + (f3 * delta);
        }
    }

    public void updateCollapse(boolean value, boolean animated) {
        ValueAnimator valueAnimator = this.collapseAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.collapseAnimator = null;
        }
        final float fromValue = this.expandT;
        final float toValue = value ? 1.0f : 0.0f;
        if (!animated) {
            this.expandT = toValue;
            forceLayout();
            return;
        }
        if (toValue > 0.0f) {
            didExtend();
        }
        float fullHeight = textHeight();
        float collapsedHeight = Math.min(COLLAPSED_HEIGHT, fullHeight);
        float fromHeight = AndroidUtilities.lerp(collapsedHeight, fullHeight, fromValue);
        float toHeight = AndroidUtilities.lerp(collapsedHeight, fullHeight, toValue);
        Math.abs(toHeight - fromHeight);
        this.collapseAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        float duration = Math.abs(fromValue - toValue) * 1250.0f * 2.0f;
        final SpringInterpolator spring = new SpringInterpolator(380.0f, 20.17f);
        final AtomicReference<Float> lastValue = new AtomicReference<>(Float.valueOf(fromValue));
        this.collapseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.AboutLinkCell$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                AboutLinkCell.this.m1628lambda$updateCollapse$1$orgtelegramuiCellsAboutLinkCell(lastValue, fromValue, toValue, spring, valueAnimator2);
            }
        });
        this.collapseAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.AboutLinkCell.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AboutLinkCell.this.didResizeEnd();
                if (AboutLinkCell.this.container.getBackground() == null) {
                    AboutLinkCell.this.container.setBackground(AboutLinkCell.this.rippleBackground);
                }
                AboutLinkCell.this.expanded = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                AboutLinkCell.this.didResizeStart();
            }
        });
        this.collapseAnimator.setDuration(duration);
        this.collapseAnimator.start();
    }

    /* renamed from: lambda$updateCollapse$1$org-telegram-ui-Cells-AboutLinkCell */
    public /* synthetic */ void m1628lambda$updateCollapse$1$orgtelegramuiCellsAboutLinkCell(AtomicReference lastValue, float fromValue, float toValue, SpringInterpolator spring, ValueAnimator a) {
        float now = ((Float) a.getAnimatedValue()).floatValue();
        float deltaTime = (now - ((Float) lastValue.getAndSet(Float.valueOf(now))).floatValue()) * 1000.0f * 8.0f;
        this.rawCollapseT = AndroidUtilities.lerp(fromValue, toValue, ((Float) a.getAnimatedValue()).floatValue());
        float lerp = AndroidUtilities.lerp(fromValue, toValue, spring.getValue(deltaTime));
        this.expandT = lerp;
        if (lerp > 0.8f && this.container.getBackground() == null) {
            this.container.setBackground(this.rippleBackground);
        }
        this.showMoreTextBackgroundView.setAlpha(1.0f - this.expandT);
        this.bottomShadow.setAlpha((float) Math.pow(1.0f - this.expandT, 2.0d));
        updateHeight();
        this.container.invalidate();
    }

    private int fromHeight() {
        return Math.min(COLLAPSED_HEIGHT + (this.valueTextView.getVisibility() == 0 ? AndroidUtilities.dp(20.0f) : 0), textHeight());
    }

    private int updateHeight() {
        int textHeight = textHeight();
        float fromHeight = fromHeight();
        int height = this.shouldExpand ? (int) AndroidUtilities.lerp(fromHeight, textHeight, this.expandT) : textHeight;
        setHeight(height);
        return height;
    }

    private void setHeight(int height) {
        boolean newHeight;
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) getLayoutParams();
        if (lp == null) {
            newHeight = true;
            if (getMinimumHeight() == 0) {
                getHeight();
            } else {
                getMinimumHeight();
            }
            lp = new RecyclerView.LayoutParams(-1, height);
        } else {
            int wasHeight = lp.height;
            newHeight = wasHeight != height;
            lp.height = height;
        }
        if (newHeight) {
            setLayoutParams(lp);
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        checkTextLayout(View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(46.0f), false);
        int height = updateHeight();
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
    }

    private StaticLayout makeTextLayout(CharSequence string, int width) {
        if (Build.VERSION.SDK_INT >= 24) {
            return StaticLayout.Builder.obtain(string, 0, string.length(), Theme.profile_aboutTextPaint, width).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(LocaleController.isRTL ? StaticLayoutEx.ALIGN_RIGHT() : StaticLayoutEx.ALIGN_LEFT()).build();
        }
        return new StaticLayout(string, Theme.profile_aboutTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    private void checkTextLayout(int maxWidth, boolean force) {
        SpannableStringBuilder spannableStringBuilder = this.stringBuilder;
        int i = 0;
        if (spannableStringBuilder != null && (maxWidth != this.lastMaxWidth || force)) {
            StaticLayout makeTextLayout = makeTextLayout(spannableStringBuilder, maxWidth);
            this.textLayout = makeTextLayout;
            this.shouldExpand = makeTextLayout.getLineCount() >= 4;
            if (this.textLayout.getLineCount() >= 3 && this.shouldExpand) {
                int end = Math.max(this.textLayout.getLineStart(2), this.textLayout.getLineEnd(2));
                if (this.stringBuilder.charAt(end - 1) == '\n') {
                    end--;
                }
                this.needSpace = (this.stringBuilder.charAt(end + (-1)) == ' ' || this.stringBuilder.charAt(end + (-1)) == '\n') ? false : true;
                this.firstThreeLinesLayout = makeTextLayout(this.stringBuilder.subSequence(0, end), maxWidth);
                this.nextLinesLayouts = new StaticLayout[this.textLayout.getLineCount() - 3];
                this.nextLinesLayoutsPositions = new Point[this.textLayout.getLineCount() - 3];
                int lastLine = this.firstThreeLinesLayout.getLineCount() - 1;
                float x = this.firstThreeLinesLayout.getLineRight(lastLine) + (this.needSpace ? this.SPACE : 0.0f);
                this.lastInlineLine = -1;
                if (this.showMoreTextBackgroundView.getMeasuredWidth() <= 0) {
                    FrameLayout frameLayout = this.showMoreTextBackgroundView;
                    int i2 = MOST_SPEC;
                    frameLayout.measure(i2, i2);
                }
                for (int line = 3; line < this.textLayout.getLineCount(); line++) {
                    int s = this.textLayout.getLineStart(line);
                    int e = this.textLayout.getLineEnd(line);
                    StaticLayout layout = makeTextLayout(this.stringBuilder.subSequence(Math.min(s, e), Math.max(s, e)), maxWidth);
                    this.nextLinesLayouts[line - 3] = layout;
                    this.nextLinesLayoutsPositions[line - 3] = new Point();
                    if (this.lastInlineLine == -1 && x > (maxWidth - this.showMoreTextBackgroundView.getMeasuredWidth()) + this.showMoreTextBackgroundView.getPaddingLeft()) {
                        this.lastInlineLine = line - 3;
                    }
                    x += layout.getLineRight(0) + this.SPACE;
                }
                if (x < (maxWidth - this.showMoreTextBackgroundView.getMeasuredWidth()) + this.showMoreTextBackgroundView.getPaddingLeft()) {
                    this.shouldExpand = false;
                }
            }
            if (!this.shouldExpand) {
                this.firstThreeLinesLayout = null;
                this.nextLinesLayouts = null;
            }
            this.lastMaxWidth = maxWidth;
            this.container.setMinimumHeight(textHeight());
            if (this.shouldExpand && this.firstThreeLinesLayout != null) {
                int fromHeight = fromHeight() - AndroidUtilities.dp(8.0f);
                StaticLayout staticLayout = this.firstThreeLinesLayout;
                setShowMoreMarginBottom((((fromHeight - staticLayout.getLineBottom(staticLayout.getLineCount() - 1)) - this.showMoreTextBackgroundView.getPaddingBottom()) - this.showMoreTextView.getPaddingBottom()) - (this.showMoreTextView.getLayout() == null ? 0 : this.showMoreTextView.getLayout().getHeight() - this.showMoreTextView.getLayout().getLineBottom(this.showMoreTextView.getLineCount() - 1)));
            }
        }
        TextView textView = this.showMoreTextView;
        if (!this.shouldExpand) {
            i = 8;
        }
        textView.setVisibility(i);
        if (!this.shouldExpand && this.container.getBackground() == null) {
            this.container.setBackground(this.rippleBackground);
        }
        if (this.shouldExpand && this.expandT < 1.0f && this.container.getBackground() != null) {
            this.container.setBackground(null);
        }
    }

    private int textHeight() {
        StaticLayout staticLayout = this.textLayout;
        int height = (staticLayout != null ? staticLayout.getHeight() : AndroidUtilities.dp(20.0f)) + AndroidUtilities.dp(16.0f);
        if (this.valueTextView.getVisibility() == 0) {
            return height + AndroidUtilities.dp(23.0f);
        }
        return height;
    }

    public boolean onClick() {
        if (this.shouldExpand && this.expandT <= 0.0f) {
            updateCollapse(true, true);
            return true;
        }
        return false;
    }

    private float easeInOutCubic(float x) {
        return ((double) x) < 0.5d ? 4.0f * x * x * x : 1.0f - (((float) Math.pow(((-2.0f) * x) + 2.0f, 3.0d)) / 2.0f);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (this.textLayout != null) {
            CharSequence text = this.stringBuilder;
            CharSequence valueText = this.valueTextView.getText();
            info.setClassName("android.widget.TextView");
            if (TextUtils.isEmpty(valueText)) {
                info.setText(text);
                return;
            }
            info.setText(((Object) valueText) + ": " + ((Object) text));
        }
    }
}
