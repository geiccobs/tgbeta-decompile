package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.util.Property;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
/* loaded from: classes4.dex */
public class TextInfoPrivacyCell extends FrameLayout {
    private int bottomPadding;
    private int fixedSize;
    private String linkTextColorKey;
    private LinkSpanDrawable.LinkCollector links;
    private final Theme.ResourcesProvider resourcesProvider;
    private CharSequence text;
    private TextView textView;
    private int topPadding;

    public TextInfoPrivacyCell(Context context) {
        this(context, 21, null);
    }

    public TextInfoPrivacyCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, 21, resourcesProvider);
    }

    public TextInfoPrivacyCell(Context context, int padding) {
        this(context, padding, null);
    }

    public TextInfoPrivacyCell(Context context, int padding, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.linkTextColorKey = Theme.key_windowBackgroundWhiteLinkText;
        this.topPadding = 10;
        this.bottomPadding = 17;
        this.resourcesProvider = resourcesProvider;
        LinkSpanDrawable.LinkCollector linkCollector = new LinkSpanDrawable.LinkCollector(this);
        this.links = linkCollector;
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, linkCollector, resourcesProvider) { // from class: org.telegram.ui.Cells.TextInfoPrivacyCell.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.LinkSpanDrawable.LinksTextView, android.widget.TextView, android.view.View
            public void onDraw(Canvas canvas) {
                TextInfoPrivacyCell.this.onTextDraw();
                super.onDraw(canvas);
                TextInfoPrivacyCell.this.afterTextDraw();
            }
        };
        this.textView = linksTextView;
        linksTextView.setTextSize(1, 14.0f);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(17.0f));
        this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        this.textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.textView.setLinkTextColor(getThemedColor(this.linkTextColorKey));
        this.textView.setImportantForAccessibility(2);
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, padding, 0.0f, padding, 0.0f));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.links != null) {
            canvas.save();
            canvas.translate(this.textView.getLeft(), this.textView.getTop());
            if (this.links.draw(canvas)) {
                invalidate();
            }
            canvas.restore();
        }
        super.onDraw(canvas);
    }

    protected void onTextDraw() {
    }

    protected void afterTextDraw() {
    }

    public void setLinkTextColorKey(String key) {
        this.linkTextColorKey = key;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.fixedSize != 0) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.fixedSize), C.BUFFER_FLAG_ENCRYPTED));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(0, 0));
        }
    }

    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    public void setBottomPadding(int value) {
        this.bottomPadding = value;
    }

    public void setFixedSize(int size) {
        this.fixedSize = size;
    }

    public void setText(CharSequence text) {
        if (!TextUtils.equals(text, this.text)) {
            this.text = text;
            if (text == null) {
                this.textView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
            } else {
                this.textView.setPadding(0, AndroidUtilities.dp(this.topPadding), 0, AndroidUtilities.dp(this.bottomPadding));
            }
            SpannableString spannableString = null;
            if (text != null) {
                int len = text.length();
                for (int i = 0; i < len - 1; i++) {
                    if (text.charAt(i) == '\n' && text.charAt(i + 1) == '\n') {
                        if (spannableString == null) {
                            spannableString = new SpannableString(text);
                        }
                        spannableString.setSpan(new AbsoluteSizeSpan(10, true), i + 1, i + 2, 33);
                    }
                }
            }
            this.textView.setText(spannableString != null ? spannableString : text);
        }
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextColor(String key) {
        this.textView.setTextColor(getThemedColor(key));
        this.textView.setTag(key);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public int length() {
        return this.textView.length();
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        float f = 1.0f;
        if (animators != null) {
            TextView textView = this.textView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr[0] = f;
            animators.add(ObjectAnimator.ofFloat(textView, property, fArr));
            return;
        }
        TextView textView2 = this.textView;
        if (!value) {
            f = 0.5f;
        }
        textView2.setAlpha(f);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(TextView.class.getName());
        info.setText(this.text);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
