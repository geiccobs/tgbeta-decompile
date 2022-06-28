package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import com.google.android.exoplayer2.C;
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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.ViewHelper;
/* loaded from: classes4.dex */
public class ArchivedStickerSetCell extends FrameLayout implements Checkable {
    private final ProgressButton addButton;
    private AnimatorSet animatorSet;
    private final boolean checkable;
    private boolean checked;
    private Button currentButton;
    private final Button deleteButton;
    private final BackupImageView imageView;
    private boolean needDivider;
    private OnCheckedChangeListener onCheckedChangeListener;
    private TLRPC.StickerSetCovered stickersSet;
    private final TextView textView;
    private final TextView valueTextView;

    /* loaded from: classes4.dex */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(ArchivedStickerSetCell archivedStickerSetCell, boolean z);
    }

    public ArchivedStickerSetCell(Context context, boolean checkable) {
        super(context);
        this.checkable = checkable;
        if (checkable) {
            ProgressButton progressButton = new ProgressButton(context);
            this.addButton = progressButton;
            this.currentButton = progressButton;
            progressButton.setText(LocaleController.getString("Add", R.string.Add));
            progressButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            progressButton.setProgressColor(Theme.getColor(Theme.key_featuredStickers_buttonProgress));
            progressButton.setBackgroundRoundRect(Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed));
            addView(progressButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
            int minWidth = AndroidUtilities.dp(60.0f);
            ProgressButton progressButton2 = new ProgressButton(context);
            this.deleteButton = progressButton2;
            progressButton2.setAllCaps(false);
            progressButton2.setMinWidth(minWidth);
            progressButton2.setMinimumWidth(minWidth);
            progressButton2.setTextSize(1, 14.0f);
            progressButton2.setTextColor(Theme.getColor(Theme.key_featuredStickers_removeButtonText));
            progressButton2.setText(LocaleController.getString("StickersRemove", R.string.StickersRemove));
            progressButton2.setBackground(Theme.getRoundRectSelectorDrawable(Theme.getColor(Theme.key_featuredStickers_removeButtonText)));
            progressButton2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            ViewHelper.setPadding(progressButton2, 8.0f, 0.0f, 8.0f, 0.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                progressButton2.setOutlineProvider(null);
            }
            addView(progressButton2, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
            View.OnClickListener toggleListener = new View.OnClickListener() { // from class: org.telegram.ui.Cells.ArchivedStickerSetCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ArchivedStickerSetCell.this.m1633lambda$new$0$orgtelegramuiCellsArchivedStickerSetCell(view);
                }
            };
            progressButton.setOnClickListener(toggleListener);
            progressButton2.setOnClickListener(toggleListener);
            syncButtons(false);
        } else {
            this.addButton = null;
            this.deleteButton = null;
        }
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(1, 16.0f);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, GravityCompat.START, 71.0f, 10.0f, 21.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.valueTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        textView2.setTextSize(1, 13.0f);
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(textView2, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, GravityCompat.START, 71.0f, 35.0f, 21.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        backupImageView.setLayerNum(1);
        addView(backupImageView, LayoutHelper.createFrameRelatively(48.0f, 48.0f, 8388659, 12.0f, 8.0f, 0.0f, 0.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-ArchivedStickerSetCell */
    public /* synthetic */ void m1633lambda$new$0$orgtelegramuiCellsArchivedStickerSetCell(View v) {
        toggle();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
    }

    @Override // android.view.ViewGroup
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (this.checkable && child == this.textView) {
            widthUsed += Math.max(this.addButton.getMeasuredWidth(), this.deleteButton.getMeasuredWidth());
        }
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
        }
    }

    public void setDrawProgress(boolean drawProgress, boolean animated) {
        ProgressButton progressButton = this.addButton;
        if (progressButton != null) {
            progressButton.setDrawProgress(drawProgress, animated);
        }
    }

    public void setStickersSet(TLRPC.StickerSetCovered set, boolean divider) {
        TLRPC.Document sticker;
        TLObject object;
        ImageLocation imageLocation;
        this.needDivider = divider;
        this.stickersSet = set;
        setWillNotDraw(!divider);
        this.textView.setText(this.stickersSet.set.title);
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
            if ((object instanceof TLRPC.Document) && MessageObject.isAnimatedStickerDocument(sticker, true)) {
                if (svgThumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(sticker), "50_50", svgThumb, 0, set);
                    return;
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(sticker), "50_50", imageLocation, (String) null, 0, set);
                    return;
                }
            } else if (imageLocation != null && imageLocation.imageType == 1) {
                this.imageView.setImage(imageLocation, "50_50", "tgs", svgThumb, set);
                return;
            } else {
                this.imageView.setImage(imageLocation, "50_50", "webp", svgThumb, set);
                return;
            }
        }
        this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, set);
    }

    public TLRPC.StickerSetCovered getStickersSet() {
        return this.stickersSet;
    }

    private void syncButtons(boolean animated) {
        if (this.checkable) {
            AnimatorSet animatorSet = this.animatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            boolean z = this.checked;
            float addButtonValue = 1.0f;
            float deleteButtonValue = z ? 1.0f : 0.0f;
            if (z) {
                addButtonValue = 0.0f;
            }
            int i = 4;
            if (animated) {
                this.currentButton = z ? this.deleteButton : this.addButton;
                this.addButton.setVisibility(0);
                this.deleteButton.setVisibility(0);
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animatorSet = animatorSet2;
                animatorSet2.setDuration(250L);
                this.animatorSet.playTogether(ObjectAnimator.ofFloat(this.deleteButton, View.ALPHA, deleteButtonValue), ObjectAnimator.ofFloat(this.deleteButton, View.SCALE_X, deleteButtonValue), ObjectAnimator.ofFloat(this.deleteButton, View.SCALE_Y, deleteButtonValue), ObjectAnimator.ofFloat(this.addButton, View.ALPHA, addButtonValue), ObjectAnimator.ofFloat(this.addButton, View.SCALE_X, addButtonValue), ObjectAnimator.ofFloat(this.addButton, View.SCALE_Y, addButtonValue));
                this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.ArchivedStickerSetCell.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (ArchivedStickerSetCell.this.currentButton == ArchivedStickerSetCell.this.addButton) {
                            ArchivedStickerSetCell.this.deleteButton.setVisibility(4);
                        } else {
                            ArchivedStickerSetCell.this.addButton.setVisibility(4);
                        }
                    }
                });
                this.animatorSet.setInterpolator(new OvershootInterpolator(1.02f));
                this.animatorSet.start();
                return;
            }
            this.deleteButton.setVisibility(z ? 0 : 4);
            this.deleteButton.setAlpha(deleteButtonValue);
            this.deleteButton.setScaleX(deleteButtonValue);
            this.deleteButton.setScaleY(deleteButtonValue);
            ProgressButton progressButton = this.addButton;
            if (!this.checked) {
                i = 0;
            }
            progressButton.setVisibility(i);
            this.addButton.setAlpha(addButtonValue);
            this.addButton.setScaleX(addButtonValue);
            this.addButton.setScaleY(addButtonValue);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    public void setChecked(boolean checked, boolean animated) {
        setChecked(checked, animated, true);
    }

    public void setChecked(boolean checked, boolean animated, boolean notify) {
        OnCheckedChangeListener onCheckedChangeListener;
        if (this.checkable && this.checked != checked) {
            this.checked = checked;
            syncButtons(animated);
            if (notify && (onCheckedChangeListener = this.onCheckedChangeListener) != null) {
                onCheckedChangeListener.onCheckedChanged(this, checked);
            }
        }
    }

    @Override // android.widget.Checkable
    public boolean isChecked() {
        return this.checked;
    }

    @Override // android.widget.Checkable
    public void toggle() {
        if (this.checkable) {
            setChecked(!isChecked());
        }
    }
}
