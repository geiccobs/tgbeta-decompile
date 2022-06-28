package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;
/* loaded from: classes4.dex */
public class FeaturedStickerSetCell extends FrameLayout {
    private ProgressButton addButton;
    private ImageView checkImage;
    private int currentAccount = UserConfig.selectedAccount;
    private AnimatorSet currentAnimation;
    private BackupImageView imageView;
    private boolean isInstalled;
    private boolean needDivider;
    private TLRPC.StickerSetCovered stickersSet;
    private TextView textView;
    private TextView valueTextView;
    private boolean wasLayout;

    public FeaturedStickerSetCell(Context context) {
        super(context);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 22.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 22.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.valueTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 100.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 100.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        ProgressButton progressButton = new ProgressButton(context);
        this.addButton = progressButton;
        progressButton.setText(LocaleController.getString("Add", R.string.Add));
        this.addButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.addButton.setProgressColor(Theme.getColor(Theme.key_featuredStickers_buttonProgress));
        this.addButton.setBackgroundRoundRect(Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed));
        addView(this.addButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.checkImage = imageView;
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.MULTIPLY));
        this.checkImage.setImageResource(R.drawable.sticker_added);
        addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
        measureChildWithMargins(this.textView, widthMeasureSpec, this.addButton.getMeasuredWidth(), heightMeasureSpec, 0);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int l = (this.addButton.getLeft() + (this.addButton.getMeasuredWidth() / 2)) - (this.checkImage.getMeasuredWidth() / 2);
        int t = (this.addButton.getTop() + (this.addButton.getMeasuredHeight() / 2)) - (this.checkImage.getMeasuredHeight() / 2);
        ImageView imageView = this.checkImage;
        imageView.layout(l, t, imageView.getMeasuredWidth() + l, this.checkImage.getMeasuredHeight() + t);
        this.wasLayout = true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    public void setStickersSet(TLRPC.StickerSetCovered set, boolean divider, boolean unread) {
        TLRPC.Document sticker;
        TLObject object;
        ImageLocation imageLocation;
        boolean sameSet = set == this.stickersSet && this.wasLayout;
        this.needDivider = divider;
        this.stickersSet = set;
        setWillNotDraw(!divider);
        this.textView.setText(this.stickersSet.set.title);
        if (unread) {
            Drawable drawable = new Drawable() { // from class: org.telegram.ui.Cells.FeaturedStickerSetCell.1
                Paint paint = new Paint(1);

                @Override // android.graphics.drawable.Drawable
                public void draw(Canvas canvas) {
                    this.paint.setColor(-12277526);
                    canvas.drawCircle(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(3.0f), this.paint);
                }

                @Override // android.graphics.drawable.Drawable
                public void setAlpha(int alpha) {
                }

                @Override // android.graphics.drawable.Drawable
                public void setColorFilter(ColorFilter colorFilter) {
                }

                @Override // android.graphics.drawable.Drawable
                public int getOpacity() {
                    return -2;
                }

                @Override // android.graphics.drawable.Drawable
                public int getIntrinsicWidth() {
                    return AndroidUtilities.dp(12.0f);
                }

                @Override // android.graphics.drawable.Drawable
                public int getIntrinsicHeight() {
                    return AndroidUtilities.dp(8.0f);
                }
            };
            this.textView.setCompoundDrawablesWithIntrinsicBounds(LocaleController.isRTL ? null : drawable, (Drawable) null, LocaleController.isRTL ? drawable : null, (Drawable) null);
        } else {
            this.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", set.set.count, new Object[0]));
        if (set.cover != null) {
            sticker = set.cover;
        } else if (!set.covers.isEmpty()) {
            sticker = set.covers.get(0);
        } else {
            sticker = null;
        }
        if (sticker != null) {
            TLObject object2 = FileLoader.getClosestPhotoSizeWithSize(set.set.thumbs, 90);
            if (object2 != null) {
                object = object2;
            } else {
                object = sticker;
            }
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(set.set.thumbs, Theme.key_windowBackgroundGray, 1.0f);
            if (object instanceof TLRPC.Document) {
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(sticker.thumbs, 90);
                imageLocation = ImageLocation.getForDocument(thumb, sticker);
            } else {
                TLRPC.PhotoSize thumb2 = (TLRPC.PhotoSize) object;
                imageLocation = ImageLocation.getForSticker(thumb2, sticker, set.set.thumb_version);
            }
            if (!(object instanceof TLRPC.Document) || !MessageObject.isAnimatedStickerDocument(sticker, true)) {
                ImageLocation imageLocation2 = imageLocation;
                if (imageLocation2 != null && imageLocation2.imageType == 1) {
                    this.imageView.setImage(imageLocation2, "50_50", "tgs", svgThumb, set);
                } else {
                    this.imageView.setImage(imageLocation2, "50_50", "webp", svgThumb, set);
                }
            } else if (svgThumb != null) {
                this.imageView.setImage(ImageLocation.getForDocument(sticker), "50_50", svgThumb, 0, set);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(sticker), "50_50", imageLocation, (String) null, 0, set);
            }
        } else {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, set);
        }
        if (sameSet) {
            boolean wasInstalled = this.isInstalled;
            boolean isStickerPackInstalled = MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(set.set.id);
            this.isInstalled = isStickerPackInstalled;
            if (isStickerPackInstalled) {
                if (!wasInstalled) {
                    this.checkImage.setVisibility(0);
                    this.addButton.setClickable(false);
                    AnimatorSet animatorSet = this.currentAnimation;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.currentAnimation = animatorSet2;
                    animatorSet2.setDuration(200L);
                    this.currentAnimation.playTogether(ObjectAnimator.ofFloat(this.addButton, "alpha", 1.0f, 0.0f), ObjectAnimator.ofFloat(this.addButton, "scaleX", 1.0f, 0.01f), ObjectAnimator.ofFloat(this.addButton, "scaleY", 1.0f, 0.01f), ObjectAnimator.ofFloat(this.checkImage, "alpha", 0.0f, 1.0f), ObjectAnimator.ofFloat(this.checkImage, "scaleX", 0.01f, 1.0f), ObjectAnimator.ofFloat(this.checkImage, "scaleY", 0.01f, 1.0f));
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.FeaturedStickerSetCell.2
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                                FeaturedStickerSetCell.this.addButton.setVisibility(4);
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationCancel(Animator animator) {
                            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                                FeaturedStickerSetCell.this.currentAnimation = null;
                            }
                        }
                    });
                    this.currentAnimation.start();
                    return;
                }
                return;
            } else if (wasInstalled) {
                this.addButton.setVisibility(0);
                this.addButton.setClickable(true);
                AnimatorSet animatorSet3 = this.currentAnimation;
                if (animatorSet3 != null) {
                    animatorSet3.cancel();
                }
                AnimatorSet animatorSet4 = new AnimatorSet();
                this.currentAnimation = animatorSet4;
                animatorSet4.setDuration(200L);
                this.currentAnimation.playTogether(ObjectAnimator.ofFloat(this.checkImage, "alpha", 1.0f, 0.0f), ObjectAnimator.ofFloat(this.checkImage, "scaleX", 1.0f, 0.01f), ObjectAnimator.ofFloat(this.checkImage, "scaleY", 1.0f, 0.01f), ObjectAnimator.ofFloat(this.addButton, "alpha", 0.0f, 1.0f), ObjectAnimator.ofFloat(this.addButton, "scaleX", 0.01f, 1.0f), ObjectAnimator.ofFloat(this.addButton, "scaleY", 0.01f, 1.0f));
                this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.FeaturedStickerSetCell.3
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                            FeaturedStickerSetCell.this.checkImage.setVisibility(4);
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                        if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                            FeaturedStickerSetCell.this.currentAnimation = null;
                        }
                    }
                });
                this.currentAnimation.start();
                return;
            } else {
                return;
            }
        }
        AnimatorSet animatorSet5 = this.currentAnimation;
        if (animatorSet5 != null) {
            animatorSet5.cancel();
        }
        boolean isStickerPackInstalled2 = MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(set.set.id);
        this.isInstalled = isStickerPackInstalled2;
        if (isStickerPackInstalled2) {
            this.addButton.setVisibility(4);
            this.addButton.setClickable(false);
            this.checkImage.setVisibility(0);
            this.checkImage.setScaleX(1.0f);
            this.checkImage.setScaleY(1.0f);
            this.checkImage.setAlpha(1.0f);
            return;
        }
        this.addButton.setVisibility(0);
        this.addButton.setClickable(true);
        this.checkImage.setVisibility(4);
        this.addButton.setScaleX(1.0f);
        this.addButton.setScaleY(1.0f);
        this.addButton.setAlpha(1.0f);
    }

    public TLRPC.StickerSetCovered getStickerSet() {
        return this.stickersSet;
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        this.addButton.setOnClickListener(onClickListener);
    }

    public void setDrawProgress(boolean value, boolean animated) {
        this.addButton.setDrawProgress(value, animated);
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
        }
    }
}
