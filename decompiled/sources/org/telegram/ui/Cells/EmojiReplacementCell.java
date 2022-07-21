package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes3.dex */
public class EmojiReplacementCell extends FrameLayout {
    private String emoji;
    private AnimatedEmojiDrawable emojiDrawable;
    private ImageView imageView;
    private final Theme.ResourcesProvider resourcesProvider;

    public EmojiReplacementCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.imageView, LayoutHelper.createFrame(42, 42.0f, 1, 0.0f, 5.0f, 0.0f, 0.0f));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(52.0f) + getPaddingLeft() + getPaddingRight(), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0f), 1073741824));
    }

    public void setEmoji(String str, int i) {
        this.emoji = str;
        if (str != null && str.startsWith("animated_")) {
            try {
                long parseLong = Long.parseLong(this.emoji.substring(9));
                AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
                if (animatedEmojiDrawable == null || animatedEmojiDrawable.getDocumentId() != parseLong) {
                    AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, 1, parseLong);
                    this.emojiDrawable = make;
                    make.addView(this);
                }
            } catch (Exception unused) {
            }
        } else {
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.emojiDrawable;
            if (animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.removeView(this);
                this.emojiDrawable = null;
            }
        }
        if (this.emojiDrawable == null) {
            this.imageView.setImageDrawable(Emoji.getEmojiBigDrawable(str));
        } else {
            this.imageView.setImageDrawable(null);
        }
        if (i == -1) {
            setBackgroundResource(R.drawable.stickers_back_left);
            setPadding(AndroidUtilities.dp(7.0f), 0, 0, 0);
        } else if (i == 0) {
            setBackgroundResource(R.drawable.stickers_back_center);
            setPadding(0, 0, 0, 0);
        } else if (i == 1) {
            setBackgroundResource(R.drawable.stickers_back_right);
            setPadding(0, 0, AndroidUtilities.dp(7.0f), 0);
        } else if (i == 2) {
            setBackgroundResource(R.drawable.stickers_back_all);
            setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        }
        Drawable background = getBackground();
        if (background != null) {
            background.setAlpha(230);
            background.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_stickersHintPanel"), PorterDuff.Mode.MULTIPLY));
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.emojiDrawable != null) {
            int dp = AndroidUtilities.dp(38.0f);
            this.emojiDrawable.setBounds((getWidth() - dp) / 2, (getHeight() - dp) / 2, (getWidth() + dp) / 2, (getHeight() + dp) / 2);
            this.emojiDrawable.draw(canvas);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.removeView(this);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.addView(this);
        }
    }

    public String getEmoji() {
        return this.emoji;
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        this.imageView.invalidate();
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
