package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import java.util.List;
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
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class FeaturedStickerSetCell2 extends FrameLayout {
    private final ProgressButton addButton;
    private final int currentAccount = UserConfig.selectedAccount;
    private AnimatorSet currentAnimation;
    private final TextView delButton;
    private final BackupImageView imageView;
    private boolean isInstalled;
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private TLRPC.StickerSetCovered stickersSet;
    private final TextView textView;
    private final TextView valueTextView;

    public FeaturedStickerSetCell2(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(1, 16.0f);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 22.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 22.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.valueTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        textView2.setTextSize(1, 13.0f);
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(textView2, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 100.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 100.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        backupImageView.setLayerNum(1);
        addView(backupImageView, LayoutHelper.createFrame(48, 48.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        ProgressButton progressButton = new ProgressButton(context);
        this.addButton = progressButton;
        progressButton.setText(LocaleController.getString("Add", R.string.Add));
        progressButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        addView(progressButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.delButton = textView3;
        textView3.setGravity(17);
        textView3.setTextColor(Theme.getColor(Theme.key_featuredStickers_removeButtonText));
        textView3.setTextSize(1, 14.0f);
        textView3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView3.setText(LocaleController.getString("StickersRemove", R.string.StickersRemove));
        addView(textView3, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f));
        updateColors();
    }

    public TextView getTextView() {
        return this.textView;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
        int width = this.addButton.getMeasuredWidth();
        int width2 = this.delButton.getMeasuredWidth();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.delButton.getLayoutParams();
        if (width2 < width) {
            layoutParams.rightMargin = AndroidUtilities.dp(14.0f) + ((width - width2) / 2);
        } else {
            layoutParams.rightMargin = AndroidUtilities.dp(14.0f);
        }
        measureChildWithMargins(this.textView, widthMeasureSpec, width, heightMeasureSpec, 0);
    }

    public void setStickersSet(TLRPC.StickerSetCovered set, boolean divider, boolean unread, boolean forceInstalled, boolean animated) {
        TLRPC.Document sticker;
        TLObject object;
        ImageLocation imageLocation;
        AnimatorSet animatorSet = this.currentAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentAnimation = null;
        }
        this.needDivider = divider;
        this.stickersSet = set;
        setWillNotDraw(!divider);
        this.textView.setText(this.stickersSet.set.title);
        if (unread) {
            Drawable drawable = new Drawable() { // from class: org.telegram.ui.Cells.FeaturedStickerSetCell2.1
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
            if (MessageObject.canAutoplayAnimatedSticker(sticker)) {
                TLObject object2 = FileLoader.getClosestPhotoSizeWithSize(set.set.thumbs, 90);
                if (object2 != null) {
                    object = object2;
                } else {
                    object = sticker;
                }
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(set.set.thumbs, Theme.key_windowBackgroundGray, 1.0f);
                if (object instanceof TLRPC.Document) {
                    imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(sticker.thumbs, 90), sticker);
                } else {
                    imageLocation = ImageLocation.getForSticker((TLRPC.PhotoSize) object, sticker, set.set.thumb_version);
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
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(sticker.thumbs, 90);
                if (thumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(thumb, sticker), "50_50", "webp", (Drawable) null, set);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(sticker), "50_50", "webp", (Drawable) null, set);
                }
            }
        } else {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, set);
        }
        this.addButton.setVisibility(0);
        boolean z = forceInstalled || MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(set.set.id);
        this.isInstalled = z;
        if (animated) {
            if (z) {
                this.delButton.setVisibility(0);
            } else {
                this.addButton.setVisibility(0);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentAnimation = animatorSet2;
            animatorSet2.setDuration(250L);
            AnimatorSet animatorSet3 = this.currentAnimation;
            Animator[] animatorArr = new Animator[6];
            TextView textView = this.delButton;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = this.isInstalled ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(textView, property, fArr);
            TextView textView2 = this.delButton;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = this.isInstalled ? 1.0f : 0.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(textView2, property2, fArr2);
            TextView textView3 = this.delButton;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = this.isInstalled ? 1.0f : 0.0f;
            animatorArr[2] = ObjectAnimator.ofFloat(textView3, property3, fArr3);
            ProgressButton progressButton = this.addButton;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = this.isInstalled ? 0.0f : 1.0f;
            animatorArr[3] = ObjectAnimator.ofFloat(progressButton, property4, fArr4);
            ProgressButton progressButton2 = this.addButton;
            Property property5 = View.SCALE_X;
            float[] fArr5 = new float[1];
            fArr5[0] = this.isInstalled ? 0.0f : 1.0f;
            animatorArr[4] = ObjectAnimator.ofFloat(progressButton2, property5, fArr5);
            ProgressButton progressButton3 = this.addButton;
            Property property6 = View.SCALE_Y;
            float[] fArr6 = new float[1];
            fArr6[0] = this.isInstalled ? 0.0f : 1.0f;
            animatorArr[5] = ObjectAnimator.ofFloat(progressButton3, property6, fArr6);
            animatorSet3.playTogether(animatorArr);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.FeaturedStickerSetCell2.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (FeaturedStickerSetCell2.this.isInstalled) {
                        FeaturedStickerSetCell2.this.addButton.setVisibility(4);
                    } else {
                        FeaturedStickerSetCell2.this.delButton.setVisibility(4);
                    }
                }
            });
            this.currentAnimation.setInterpolator(new OvershootInterpolator(1.02f));
            this.currentAnimation.start();
        } else if (z) {
            this.delButton.setVisibility(0);
            this.delButton.setAlpha(1.0f);
            this.delButton.setScaleX(1.0f);
            this.delButton.setScaleY(1.0f);
            this.addButton.setVisibility(4);
            this.addButton.setAlpha(0.0f);
            this.addButton.setScaleX(0.0f);
            this.addButton.setScaleY(0.0f);
        } else {
            this.addButton.setVisibility(0);
            this.addButton.setAlpha(1.0f);
            this.addButton.setScaleX(1.0f);
            this.addButton.setScaleY(1.0f);
            this.delButton.setVisibility(4);
            this.delButton.setAlpha(0.0f);
            this.delButton.setScaleX(0.0f);
            this.delButton.setScaleY(0.0f);
        }
    }

    public TLRPC.StickerSetCovered getStickerSet() {
        return this.stickersSet;
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        this.addButton.setOnClickListener(onClickListener);
        this.delButton.setOnClickListener(onClickListener);
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
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(71.0f), getHeight() - 1, getWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(71.0f) : 0), getHeight() - 1, Theme.dividerPaint);
        }
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public void updateColors() {
        this.addButton.setProgressColor(Theme.getColor(Theme.key_featuredStickers_buttonProgress));
        this.addButton.setBackgroundRoundRect(Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed));
    }

    public static void createThemeDescriptions(List<ThemeDescription> descriptions, RecyclerListView listView, ThemeDescription.ThemeDescriptionDelegate delegate) {
        descriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        descriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        descriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_buttonText));
        descriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetCell.class}, new String[]{"delButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_removeButtonText));
        descriptions.add(new ThemeDescription(listView, 0, new Class[]{FeaturedStickerSetCell.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_featuredStickers_buttonProgress));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_featuredStickers_addButtonPressed));
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
