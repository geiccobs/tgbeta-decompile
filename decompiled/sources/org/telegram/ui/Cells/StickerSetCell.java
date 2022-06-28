package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.ForegroundColorSpanThemable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
/* loaded from: classes4.dex */
public class StickerSetCell extends FrameLayout {
    private static final String LINK_PREFIX = "t.me/addstickers/";
    private CheckBox2 checkBox;
    private BackupImageView imageView;
    private boolean needDivider;
    private final int option;
    private ImageView optionsButton;
    private RadialProgressView progressView;
    private Rect rect = new Rect();
    private ImageView reorderButton;
    private TLRPC.TL_messages_stickerSet stickersSet;
    private TextView textView;
    private TextView valueTextView;

    public StickerSetCell(Context context, int option) {
        super(context);
        this.option = option;
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, GravityCompat.START, 71.0f, 9.0f, 46.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.valueTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(this.valueTextView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, GravityCompat.START, 71.0f, 32.0f, 46.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 13.0f, 9.0f, LocaleController.isRTL ? 13.0f : 0.0f, 0.0f));
        if (option == 2) {
            RadialProgressView radialProgressView = new RadialProgressView(getContext());
            this.progressView = radialProgressView;
            radialProgressView.setProgressColor(Theme.getColor(Theme.key_dialogProgressCircle));
            this.progressView.setSize(AndroidUtilities.dp(30.0f));
            addView(this.progressView, LayoutHelper.createFrame(48, 48.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 5.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        } else if (option != 0) {
            ImageView imageView = new ImageView(context);
            this.optionsButton = imageView;
            int i2 = 0;
            imageView.setFocusable(false);
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            if (option != 3) {
                this.optionsButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            }
            if (option == 1) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(R.drawable.msg_actions);
                this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
                addView(this.optionsButton, LayoutHelper.createFrame(40, 40, (LocaleController.isRTL ? 3 : i) | 16));
                ImageView imageView2 = new ImageView(context);
                this.reorderButton = imageView2;
                imageView2.setAlpha(0.0f);
                this.reorderButton.setVisibility(8);
                this.reorderButton.setScaleType(ImageView.ScaleType.CENTER);
                this.reorderButton.setImageResource(R.drawable.list_reorder);
                this.reorderButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), PorterDuff.Mode.MULTIPLY));
                addView(this.reorderButton, LayoutHelper.createFrameRelatively(58.0f, 58.0f, GravityCompat.END));
                CheckBox2 checkBox2 = new CheckBox2(context, 21);
                this.checkBox = checkBox2;
                checkBox2.setColor(null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
                this.checkBox.setDrawUnchecked(false);
                this.checkBox.setDrawBackgroundAsArc(3);
                addView(this.checkBox, LayoutHelper.createFrameRelatively(24.0f, 24.0f, GravityCompat.START, 34.0f, 30.0f, 0.0f, 0.0f));
            } else if (option == 3) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(R.drawable.floating_check);
                addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 10 : 0, 9.0f, !LocaleController.isRTL ? 10 : i2, 0.0f));
            }
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
    }

    public void setText(String title, String subtitle, int icon, boolean divider) {
        this.needDivider = divider;
        this.stickersSet = null;
        this.textView.setText(title);
        this.valueTextView.setText(subtitle);
        if (TextUtils.isEmpty(subtitle)) {
            this.textView.setTranslationY(AndroidUtilities.dp(10.0f));
        } else {
            this.textView.setTranslationY(0.0f);
        }
        if (icon != 0) {
            this.imageView.setImageResource(icon, Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon));
            this.imageView.setVisibility(0);
            RadialProgressView radialProgressView = this.progressView;
            if (radialProgressView != null) {
                radialProgressView.setVisibility(4);
                return;
            }
            return;
        }
        this.imageView.setVisibility(4);
        RadialProgressView radialProgressView2 = this.progressView;
        if (radialProgressView2 != null) {
            radialProgressView2.setVisibility(0);
        }
    }

    public void setNeedDivider(boolean needDivider) {
        this.needDivider = needDivider;
    }

    public void setStickersSet(TLRPC.TL_messages_stickerSet set, boolean divider) {
        setStickersSet(set, divider, false);
    }

    public void setSearchQuery(TLRPC.TL_messages_stickerSet tlSet, String query, Theme.ResourcesProvider resourcesProvider) {
        TLRPC.StickerSet set = tlSet.set;
        int titleIndex = set.title.toLowerCase(Locale.ROOT).indexOf(query);
        if (titleIndex != -1) {
            SpannableString spannableString = new SpannableString(set.title);
            spannableString.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4, resourcesProvider), titleIndex, query.length() + titleIndex, 0);
            this.textView.setText(spannableString);
        }
        int linkIndex = set.short_name.toLowerCase(Locale.ROOT).indexOf(query);
        if (linkIndex != -1) {
            int linkIndex2 = linkIndex + LINK_PREFIX.length();
            SpannableString spannableString2 = new SpannableString(LINK_PREFIX + set.short_name);
            spannableString2.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4, resourcesProvider), linkIndex2, query.length() + linkIndex2, 0);
            this.valueTextView.setText(spannableString2);
        }
    }

    public void setStickersSet(TLRPC.TL_messages_stickerSet set, boolean divider, boolean groupSearch) {
        TLObject object;
        ImageLocation imageLocation;
        this.needDivider = divider;
        this.stickersSet = set;
        this.imageView.setVisibility(0);
        RadialProgressView radialProgressView = this.progressView;
        if (radialProgressView != null) {
            radialProgressView.setVisibility(4);
        }
        this.textView.setTranslationY(0.0f);
        this.textView.setText(this.stickersSet.set.title);
        if (this.stickersSet.set.archived) {
            this.textView.setAlpha(0.5f);
            this.valueTextView.setAlpha(0.5f);
            this.imageView.setAlpha(0.5f);
        } else {
            this.textView.setAlpha(1.0f);
            this.valueTextView.setAlpha(1.0f);
            this.imageView.setAlpha(1.0f);
        }
        ArrayList<TLRPC.Document> documents = set.documents;
        if (documents == null || documents.isEmpty()) {
            this.valueTextView.setText(LocaleController.formatPluralString("Stickers", 0, new Object[0]));
            this.imageView.setImageDrawable(null);
        } else {
            this.valueTextView.setText(LocaleController.formatPluralString("Stickers", documents.size(), new Object[0]));
            TLRPC.Document sticker = documents.get(0);
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
            if (((object instanceof TLRPC.Document) && MessageObject.isAnimatedStickerDocument(sticker, true)) || MessageObject.isVideoSticker(sticker)) {
                if (svgThumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(sticker), "50_50", svgThumb, 0, set);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(sticker), "50_50", imageLocation, (String) null, 0, set);
                }
            } else if (imageLocation != null && imageLocation.imageType == 1) {
                this.imageView.setImage(imageLocation, "50_50", "tgs", svgThumb, set);
            } else {
                this.imageView.setImage(imageLocation, "50_50", "webp", svgThumb, set);
            }
        }
        if (groupSearch) {
            TextView textView = this.valueTextView;
            textView.setText(LINK_PREFIX + set.set.short_name);
        }
    }

    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    public boolean isChecked() {
        int i = this.option;
        return i == 1 ? this.checkBox.isChecked() : i == 3 && this.optionsButton.getVisibility() == 0;
    }

    public void setChecked(final boolean checked, boolean animated) {
        switch (this.option) {
            case 1:
                this.checkBox.setChecked(checked, animated);
                return;
            case 2:
            default:
                return;
            case 3:
                float f = 0.1f;
                if (animated) {
                    this.optionsButton.animate().cancel();
                    ViewPropertyAnimator scaleX = this.optionsButton.animate().setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.StickerSetCell.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (!checked) {
                                StickerSetCell.this.optionsButton.setVisibility(4);
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animation) {
                            if (checked) {
                                StickerSetCell.this.optionsButton.setVisibility(0);
                            }
                        }
                    }).alpha(checked ? 1.0f : 0.0f).scaleX(checked ? 1.0f : 0.1f);
                    if (checked) {
                        f = 1.0f;
                    }
                    scaleX.scaleY(f).setDuration(150L).start();
                    return;
                }
                this.optionsButton.setVisibility(checked ? 0 : 4);
                if (!checked) {
                    this.optionsButton.setScaleX(0.1f);
                    this.optionsButton.setScaleY(0.1f);
                    return;
                }
                return;
        }
    }

    public void setReorderable(boolean reorderable) {
        setReorderable(reorderable, true);
    }

    public void setReorderable(final boolean reorderable, boolean animated) {
        if (this.option == 1) {
            float[] alphaValues = new float[2];
            float f = 0.0f;
            float f2 = 1.0f;
            int i = 0;
            alphaValues[0] = reorderable ? 1.0f : 0.0f;
            if (!reorderable) {
                f = 1.0f;
            }
            alphaValues[1] = f;
            float[] scaleValues = new float[2];
            scaleValues[0] = reorderable ? 1.0f : 0.66f;
            if (reorderable) {
                f2 = 0.66f;
            }
            scaleValues[1] = f2;
            if (animated) {
                this.reorderButton.setVisibility(0);
                this.reorderButton.animate().alpha(alphaValues[0]).scaleX(scaleValues[0]).scaleY(scaleValues[0]).setDuration(200L).setInterpolator(Easings.easeOutSine).withEndAction(new Runnable() { // from class: org.telegram.ui.Cells.StickerSetCell$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        StickerSetCell.this.m1672lambda$setReorderable$0$orgtelegramuiCellsStickerSetCell(reorderable);
                    }
                }).start();
                this.optionsButton.setVisibility(0);
                this.optionsButton.animate().alpha(alphaValues[1]).scaleX(scaleValues[1]).scaleY(scaleValues[1]).setDuration(200L).setInterpolator(Easings.easeOutSine).withEndAction(new Runnable() { // from class: org.telegram.ui.Cells.StickerSetCell$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        StickerSetCell.this.m1673lambda$setReorderable$1$orgtelegramuiCellsStickerSetCell(reorderable);
                    }
                }).start();
                return;
            }
            this.reorderButton.setVisibility(reorderable ? 0 : 8);
            this.reorderButton.setAlpha(alphaValues[0]);
            this.reorderButton.setScaleX(scaleValues[0]);
            this.reorderButton.setScaleY(scaleValues[0]);
            ImageView imageView = this.optionsButton;
            if (reorderable) {
                i = 8;
            }
            imageView.setVisibility(i);
            this.optionsButton.setAlpha(alphaValues[1]);
            this.optionsButton.setScaleX(scaleValues[1]);
            this.optionsButton.setScaleY(scaleValues[1]);
        }
    }

    /* renamed from: lambda$setReorderable$0$org-telegram-ui-Cells-StickerSetCell */
    public /* synthetic */ void m1672lambda$setReorderable$0$orgtelegramuiCellsStickerSetCell(boolean reorderable) {
        if (!reorderable) {
            this.reorderButton.setVisibility(8);
        }
    }

    /* renamed from: lambda$setReorderable$1$org-telegram-ui-Cells-StickerSetCell */
    public /* synthetic */ void m1673lambda$setReorderable$1$orgtelegramuiCellsStickerSetCell(boolean reorderable) {
        if (reorderable) {
            this.optionsButton.setVisibility(8);
        }
    }

    public void setOnReorderButtonTouchListener(View.OnTouchListener listener) {
        this.reorderButton.setOnTouchListener(listener);
    }

    public void setOnOptionsClick(View.OnClickListener listener) {
        ImageView imageView = this.optionsButton;
        if (imageView == null) {
            return;
        }
        imageView.setOnClickListener(listener);
    }

    public TLRPC.TL_messages_stickerSet getStickersSet() {
        return this.stickersSet;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        ImageView imageView;
        if (Build.VERSION.SDK_INT >= 21 && getBackground() != null && (imageView = this.optionsButton) != null) {
            imageView.getHitRect(this.rect);
            if (this.rect.contains((int) event.getX(), (int) event.getY())) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(71.0f), getHeight() - 1, (getWidth() - getPaddingRight()) - (LocaleController.isRTL ? AndroidUtilities.dp(71.0f) : 0), getHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null && checkBox2.isChecked()) {
            info.setCheckable(true);
            info.setChecked(true);
        }
    }
}
