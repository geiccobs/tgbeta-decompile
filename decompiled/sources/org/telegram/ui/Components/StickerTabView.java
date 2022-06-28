package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SvgHelper;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class StickerTabView extends FrameLayout {
    public static final int EMOJI_TYPE = 2;
    public static final int ICON_TYPE = 1;
    private static final int IMAGE_SMALL_SIZE = 26;
    public static final int SMALL_HEIGHT = 36;
    public static final int SMALL_WIDTH = 38;
    public static final int STICKER_TYPE = 0;
    private static int indexPointer;
    public float dragOffset;
    ValueAnimator dragOffsetAnimator;
    boolean expanded;
    boolean hasSavedLeft;
    ImageView iconView;
    BackupImageView imageView;
    public final int index;
    public boolean inited;
    public boolean isChatSticker;
    float lastLeft;
    boolean roundImage;
    public SvgHelper.SvgDrawable svgThumb;
    TextView textView;
    public int type;
    View visibleView;

    public StickerTabView(Context context, int type) {
        super(context);
        this.type = type;
        int i = indexPointer;
        indexPointer = i + 1;
        this.index = i;
        if (type == 2) {
            BackupImageView backupImageView = new BackupImageView(getContext());
            this.imageView = backupImageView;
            backupImageView.setLayerNum(1);
            this.imageView.setAspectFit(false);
            addView(this.imageView, LayoutHelper.createFrame(26, 26, 17));
            this.visibleView = this.imageView;
        } else if (type == 1) {
            ImageView imageView = new ImageView(context);
            this.iconView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView(this.iconView, LayoutHelper.createFrame(24, 24, 17));
            this.visibleView = this.iconView;
        } else {
            BackupImageView backupImageView2 = new BackupImageView(getContext());
            this.imageView = backupImageView2;
            backupImageView2.setLayerNum(1);
            this.imageView.setAspectFit(true);
            addView(this.imageView, LayoutHelper.createFrame(26, 26, 17));
            this.visibleView = this.imageView;
        }
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setLines(1);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setTextSize(1, 11.0f);
        this.textView.setGravity(1);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 81, 8.0f, 0.0f, 8.0f, 10.0f));
        this.textView.setVisibility(8);
    }

    public void setExpanded(boolean expanded) {
        int i = this.type;
        if (i == 2) {
            return;
        }
        this.expanded = expanded;
        float size = i == 1 ? 24.0f : 26.0f;
        float sizeExpanded = i == 1 ? 38.0f : 56.0f;
        this.visibleView.getLayoutParams().width = AndroidUtilities.dp(expanded ? sizeExpanded : size);
        this.visibleView.getLayoutParams().height = AndroidUtilities.dp(expanded ? sizeExpanded : size);
        this.textView.setVisibility(expanded ? 0 : 8);
        if (this.type != 1 && this.roundImage) {
            this.imageView.setRoundRadius(AndroidUtilities.dp(this.visibleView.getLayoutParams().width / 2.0f));
        }
    }

    public void updateExpandProgress(float expandProgress) {
        int i = this.type;
        if (i == 2) {
            return;
        }
        if (this.expanded) {
            float size = i == 1 ? 24.0f : 26.0f;
            float sizeExpanded = i == 1 ? 38.0f : 56.0f;
            float fromX = AndroidUtilities.dp(38.0f - size) / 2.0f;
            float fromY = AndroidUtilities.dp(36.0f - size) / 2.0f;
            float toX = AndroidUtilities.dp(86.0f - sizeExpanded) / 2.0f;
            float toY = AndroidUtilities.dp(86.0f - sizeExpanded) / 2.0f;
            this.visibleView.setTranslationY(((fromY - toY) * (1.0f - expandProgress)) - (AndroidUtilities.dp(8.0f) * expandProgress));
            this.visibleView.setTranslationX((fromX - toX) * (1.0f - expandProgress));
            this.textView.setAlpha(Math.max(0.0f, (expandProgress - 0.5f) / 0.5f));
            this.textView.setTranslationY((-AndroidUtilities.dp(40.0f)) * (1.0f - expandProgress));
            this.textView.setTranslationX((-AndroidUtilities.dp(12.0f)) * (1.0f - expandProgress));
            this.visibleView.setPivotX(0.0f);
            this.visibleView.setPivotY(0.0f);
            float s = ((size / sizeExpanded) * (1.0f - expandProgress)) + expandProgress;
            this.visibleView.setScaleX(s);
            this.visibleView.setScaleY(s);
            return;
        }
        this.visibleView.setTranslationX(0.0f);
        this.visibleView.setTranslationY(0.0f);
        this.visibleView.setScaleX(1.0f);
        this.visibleView.setScaleY(1.0f);
    }

    public void saveXPosition() {
        this.lastLeft = getLeft();
        this.hasSavedLeft = true;
        invalidate();
    }

    public void animateIfPositionChanged(final ViewGroup parent) {
        float f = this.lastLeft;
        if (getLeft() != f && this.hasSavedLeft) {
            this.dragOffset = f - getLeft();
            ValueAnimator valueAnimator = this.dragOffsetAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.dragOffsetAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.dragOffset, 0.0f);
            this.dragOffsetAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.StickerTabView.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    StickerTabView.this.dragOffset = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                    StickerTabView.this.invalidate();
                    parent.invalidate();
                }
            });
            this.dragOffsetAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.StickerTabView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    StickerTabView.this.dragOffset = 0.0f;
                    StickerTabView.this.invalidate();
                    parent.invalidate();
                }
            });
            this.dragOffsetAnimator.start();
        }
        this.hasSavedLeft = false;
    }

    public void setRoundImage() {
        this.roundImage = true;
    }
}
