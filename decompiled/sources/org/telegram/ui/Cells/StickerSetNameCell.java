package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.ColorSpanUnderline;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class StickerSetNameCell extends FrameLayout {
    private ImageView buttonView;
    private boolean empty;
    private boolean isEmoji;
    private final Theme.ResourcesProvider resourcesProvider;
    private CharSequence stickerSetName;
    private int stickerSetNameSearchIndex;
    private int stickerSetNameSearchLength;
    private TextView textView;
    private CharSequence url;
    private int urlSearchLength;
    private TextView urlTextView;

    public StickerSetNameCell(Context context, boolean emoji, Theme.ResourcesProvider resourcesProvider) {
        this(context, emoji, false, resourcesProvider);
    }

    public StickerSetNameCell(Context context, boolean emoji, boolean supportRtl, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        FrameLayout.LayoutParams lp;
        FrameLayout.LayoutParams lp2;
        FrameLayout.LayoutParams lp3;
        this.resourcesProvider = resourcesProvider;
        this.isEmoji = emoji;
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(getThemedColor(Theme.key_chat_emojiPanelStickerSetName));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setSingleLine(true);
        float f = 17.0f;
        if (supportRtl) {
            lp = LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388659, emoji ? 15.0f : 17.0f, 2.0f, 57.0f, 0.0f);
        } else {
            lp = LayoutHelper.createFrame(-2, -2.0f, 51, emoji ? 15.0f : f, 2.0f, 57.0f, 0.0f);
        }
        addView(this.textView, lp);
        TextView textView2 = new TextView(context);
        this.urlTextView = textView2;
        textView2.setTextColor(getThemedColor(Theme.key_chat_emojiPanelStickerSetName));
        this.urlTextView.setTextSize(1, 12.0f);
        this.urlTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.urlTextView.setSingleLine(true);
        this.urlTextView.setVisibility(4);
        if (supportRtl) {
            lp2 = LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388661, 17.0f, 6.0f, 17.0f, 0.0f);
        } else {
            lp2 = LayoutHelper.createFrame(-2, -2.0f, 53, 17.0f, 6.0f, 17.0f, 0.0f);
        }
        addView(this.urlTextView, lp2);
        ImageView imageView = new ImageView(context);
        this.buttonView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.buttonView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelStickerSetNameIcon), PorterDuff.Mode.MULTIPLY));
        if (supportRtl) {
            lp3 = LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388661, 0.0f, 0.0f, 16.0f, 0.0f);
        } else {
            lp3 = LayoutHelper.createFrame(24, 24.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f);
        }
        addView(this.buttonView, lp3);
    }

    public void setUrl(CharSequence text, int searchLength) {
        this.url = text;
        this.urlSearchLength = searchLength;
        this.urlTextView.setVisibility(text != null ? 0 : 8);
        updateUrlSearchSpan();
    }

    private void updateUrlSearchSpan() {
        if (this.url != null) {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.url);
            try {
                builder.setSpan(new ColorSpanUnderline(getThemedColor(Theme.key_chat_emojiPanelStickerSetNameHighlight)), 0, this.urlSearchLength, 33);
                builder.setSpan(new ColorSpanUnderline(getThemedColor(Theme.key_chat_emojiPanelStickerSetName)), this.urlSearchLength, this.url.length(), 33);
            } catch (Exception e) {
            }
            this.urlTextView.setText(builder);
        }
    }

    public void setText(CharSequence text, int resId) {
        setText(text, resId, null, 0, 0);
    }

    public void setText(CharSequence text, int resId, CharSequence iconAccDescr) {
        setText(text, resId, iconAccDescr, 0, 0);
    }

    public void setTitleColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(CharSequence text, int resId, int index, int searchLength) {
        setText(text, resId, null, index, searchLength);
    }

    public void setText(CharSequence text, int resId, CharSequence iconAccDescr, int index, int searchLength) {
        this.stickerSetName = text;
        this.stickerSetNameSearchIndex = index;
        this.stickerSetNameSearchLength = searchLength;
        if (text == null) {
            this.empty = true;
            this.textView.setText("");
            this.buttonView.setVisibility(4);
            return;
        }
        if (searchLength != 0) {
            updateTextSearchSpan();
        } else {
            TextView textView = this.textView;
            textView.setText(Emoji.replaceEmoji(text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
        }
        if (resId == 0) {
            this.buttonView.setVisibility(4);
            return;
        }
        this.buttonView.setImageResource(resId);
        this.buttonView.setContentDescription(iconAccDescr);
        this.buttonView.setVisibility(0);
    }

    private void updateTextSearchSpan() {
        if (this.stickerSetName != null && this.stickerSetNameSearchLength != 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.stickerSetName);
            try {
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getThemedColor(Theme.key_chat_emojiPanelStickerSetNameHighlight));
                int i = this.stickerSetNameSearchIndex;
                builder.setSpan(foregroundColorSpan, i, this.stickerSetNameSearchLength + i, 33);
            } catch (Exception e) {
            }
            TextView textView = this.textView;
            textView.setText(Emoji.replaceEmoji(builder, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
        }
    }

    public void setOnIconClickListener(View.OnClickListener onIconClickListener) {
        this.buttonView.setOnClickListener(onIconClickListener);
    }

    @Override // android.view.View
    public void invalidate() {
        this.textView.invalidate();
        super.invalidate();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.empty) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(1, C.BUFFER_FLAG_ENCRYPTED));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.isEmoji ? 28.0f : 24.0f), C.BUFFER_FLAG_ENCRYPTED));
        }
    }

    @Override // android.view.ViewGroup
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (child == this.urlTextView) {
            widthUsed += this.textView.getMeasuredWidth() + AndroidUtilities.dp(16.0f);
        }
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    public void updateColors() {
        updateTextSearchSpan();
        updateUrlSearchSpan();
    }

    public static void createThemeDescriptions(List<ThemeDescription> descriptions, RecyclerListView listView, ThemeDescription.ThemeDescriptionDelegate delegate) {
        descriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{StickerSetNameCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_emojiPanelStickerSetName));
        descriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{StickerSetNameCell.class}, new String[]{"urlTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_emojiPanelStickerSetName));
        descriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{StickerSetNameCell.class}, new String[]{"buttonView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_emojiPanelStickerSetNameIcon));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_chat_emojiPanelStickerSetNameHighlight));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_chat_emojiPanelStickerSetName));
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
