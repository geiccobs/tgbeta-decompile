package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.ColorSpanUnderline;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class FeaturedStickerSetInfoCell extends FrameLayout {
    private ProgressButton addButton;
    private AnimatorSet animatorSet;
    private boolean canAddRemove;
    private TextView delButton;
    private boolean hasOnClick;
    private TextView infoTextView;
    private boolean isInstalled;
    private boolean isUnread;
    private TextView nameTextView;
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private TLRPC.StickerSetCovered set;
    private int stickerSetNameSearchIndex;
    private int stickerSetNameSearchLength;
    float unreadProgress;
    private CharSequence url;
    private int urlSearchLength;
    private int currentAccount = UserConfig.selectedAccount;
    private Paint paint = new Paint(1);

    public FeaturedStickerSetInfoCell(Context context, int left, boolean supportRtl, boolean canAddRemove, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        FrameLayout.LayoutParams lp;
        FrameLayout.LayoutParams lp2;
        FrameLayout.LayoutParams lp3;
        FrameLayout.LayoutParams lp4;
        this.canAddRemove = canAddRemove;
        this.resourcesProvider = resourcesProvider;
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(getThemedColor(Theme.key_chat_emojiPanelTrendingTitle));
        this.nameTextView.setTextSize(1, 17.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        if (supportRtl) {
            lp = LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388659, left, 8.0f, 40.0f, 0.0f);
        } else {
            lp = LayoutHelper.createFrame(-2, -2.0f, 51, left, 8.0f, 40.0f, 0.0f);
        }
        addView(this.nameTextView, lp);
        TextView textView2 = new TextView(context);
        this.infoTextView = textView2;
        textView2.setTextColor(getThemedColor(Theme.key_chat_emojiPanelTrendingDescription));
        this.infoTextView.setTextSize(1, 13.0f);
        this.infoTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.infoTextView.setSingleLine(true);
        if (supportRtl) {
            lp2 = LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388659, left, 30.0f, 100.0f, 0.0f);
        } else {
            lp2 = LayoutHelper.createFrame(-2, -2.0f, 51, left, 30.0f, 100.0f, 0.0f);
        }
        addView(this.infoTextView, lp2);
        if (canAddRemove) {
            ProgressButton progressButton = new ProgressButton(context);
            this.addButton = progressButton;
            progressButton.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
            this.addButton.setText(LocaleController.getString("Add", R.string.Add));
            if (supportRtl) {
                lp3 = LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f);
            } else {
                lp3 = LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f);
            }
            addView(this.addButton, lp3);
            TextView textView3 = new TextView(context);
            this.delButton = textView3;
            textView3.setGravity(17);
            this.delButton.setTextColor(getThemedColor(Theme.key_featuredStickers_removeButtonText));
            this.delButton.setTextSize(1, 14.0f);
            this.delButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.delButton.setText(LocaleController.getString("StickersRemove", R.string.StickersRemove));
            if (supportRtl) {
                lp4 = LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f);
            } else {
                lp4 = LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f);
            }
            addView(this.delButton, lp4);
        }
        setWillNotDraw(false);
        updateColors();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), C.BUFFER_FLAG_ENCRYPTED));
        if (this.canAddRemove) {
            int width = this.addButton.getMeasuredWidth();
            int width2 = this.delButton.getMeasuredWidth();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.delButton.getLayoutParams();
            if (width2 < width) {
                layoutParams.rightMargin = AndroidUtilities.dp(14.0f) + ((width - width2) / 2);
            } else {
                layoutParams.rightMargin = AndroidUtilities.dp(14.0f);
            }
            measureChildWithMargins(this.nameTextView, widthMeasureSpec, width, heightMeasureSpec, 0);
        }
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        if (this.canAddRemove) {
            this.hasOnClick = true;
            this.addButton.setOnClickListener(onClickListener);
            this.delButton.setOnClickListener(onClickListener);
        }
    }

    public void setAddDrawProgress(boolean drawProgress, boolean animated) {
        if (this.canAddRemove) {
            this.addButton.setDrawProgress(drawProgress, animated);
        }
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSet, boolean unread) {
        setStickerSet(stickerSet, unread, false, 0, 0, false);
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSet, boolean unread, boolean animated) {
        setStickerSet(stickerSet, unread, animated, 0, 0, false);
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSet, boolean unread, boolean animated, int index, int searchLength) {
        setStickerSet(stickerSet, unread, animated, index, searchLength, false);
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSet, boolean unread, boolean animated, int index, int searchLength, boolean forceInstalled) {
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        float f = 1.0f;
        if (this.set != stickerSet) {
            this.unreadProgress = unread ? 1.0f : 0.0f;
            invalidate();
        }
        this.set = stickerSet;
        this.stickerSetNameSearchIndex = index;
        this.stickerSetNameSearchLength = searchLength;
        if (searchLength != 0) {
            updateStickerSetNameSearchSpan();
        } else {
            this.nameTextView.setText(stickerSet.set.title);
        }
        this.infoTextView.setText(LocaleController.formatPluralString("Stickers", stickerSet.set.count, new Object[0]));
        this.isUnread = unread;
        if (this.canAddRemove) {
            if (this.hasOnClick) {
                this.addButton.setVisibility(0);
                boolean z = forceInstalled || MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(stickerSet.set.id);
                this.isInstalled = z;
                if (animated) {
                    if (z) {
                        this.delButton.setVisibility(0);
                    } else {
                        this.addButton.setVisibility(0);
                    }
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.animatorSet = animatorSet2;
                    animatorSet2.setDuration(250L);
                    AnimatorSet animatorSet3 = this.animatorSet;
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
                    if (this.isInstalled) {
                        f = 0.0f;
                    }
                    fArr6[0] = f;
                    animatorArr[5] = ObjectAnimator.ofFloat(progressButton3, property6, fArr6);
                    animatorSet3.playTogether(animatorArr);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.FeaturedStickerSetInfoCell.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (FeaturedStickerSetInfoCell.this.isInstalled) {
                                FeaturedStickerSetInfoCell.this.addButton.setVisibility(4);
                            } else {
                                FeaturedStickerSetInfoCell.this.delButton.setVisibility(4);
                            }
                        }
                    });
                    this.animatorSet.setInterpolator(new OvershootInterpolator(1.02f));
                    this.animatorSet.start();
                    return;
                } else if (z) {
                    this.delButton.setVisibility(0);
                    this.delButton.setAlpha(1.0f);
                    this.delButton.setScaleX(1.0f);
                    this.delButton.setScaleY(1.0f);
                    this.addButton.setVisibility(4);
                    this.addButton.setAlpha(0.0f);
                    this.addButton.setScaleX(0.0f);
                    this.addButton.setScaleY(0.0f);
                    return;
                } else {
                    this.addButton.setVisibility(0);
                    this.addButton.setAlpha(1.0f);
                    this.addButton.setScaleX(1.0f);
                    this.addButton.setScaleY(1.0f);
                    this.delButton.setVisibility(4);
                    this.delButton.setAlpha(0.0f);
                    this.delButton.setScaleX(0.0f);
                    this.delButton.setScaleY(0.0f);
                    return;
                }
            }
            this.addButton.setVisibility(8);
        }
    }

    private void updateStickerSetNameSearchSpan() {
        if (this.stickerSetNameSearchLength != 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.set.set.title);
            try {
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getThemedColor(Theme.key_windowBackgroundWhiteBlueText4));
                int i = this.stickerSetNameSearchIndex;
                builder.setSpan(foregroundColorSpan, i, this.stickerSetNameSearchLength + i, 33);
            } catch (Exception e) {
            }
            this.nameTextView.setText(builder);
        }
    }

    public void setUrl(CharSequence text, int searchLength) {
        this.url = text;
        this.urlSearchLength = searchLength;
        updateUrlSearchSpan();
    }

    private void updateUrlSearchSpan() {
        if (this.url != null) {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.url);
            try {
                builder.setSpan(new ColorSpanUnderline(getThemedColor(Theme.key_windowBackgroundWhiteBlueText4)), 0, this.urlSearchLength, 33);
                builder.setSpan(new ColorSpanUnderline(getThemedColor(Theme.key_chat_emojiPanelTrendingDescription)), this.urlSearchLength, this.url.length(), 33);
            } catch (Exception e) {
            }
            this.infoTextView.setText(builder);
        }
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    public TLRPC.StickerSetCovered getStickerSet() {
        return this.set;
    }

    public boolean isNeedDivider() {
        return this.needDivider;
    }

    public void setNeedDivider(boolean needDivider) {
        this.needDivider = needDivider;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        boolean z = this.isUnread;
        if (z || this.unreadProgress != 0.0f) {
            if (z) {
                float f = this.unreadProgress;
                if (f != 1.0f) {
                    float f2 = f + 0.16f;
                    this.unreadProgress = f2;
                    if (f2 > 1.0f) {
                        this.unreadProgress = 1.0f;
                    } else {
                        invalidate();
                    }
                    this.paint.setColor(getThemedColor(Theme.key_featuredStickers_unread));
                    canvas.drawCircle(this.nameTextView.getRight() + AndroidUtilities.dp(12.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f) * this.unreadProgress, this.paint);
                }
            }
            if (!z) {
                float f3 = this.unreadProgress;
                if (f3 != 0.0f) {
                    float f4 = f3 - 0.16f;
                    this.unreadProgress = f4;
                    if (f4 < 0.0f) {
                        this.unreadProgress = 0.0f;
                    } else {
                        invalidate();
                    }
                }
            }
            this.paint.setColor(getThemedColor(Theme.key_featuredStickers_unread));
            canvas.drawCircle(this.nameTextView.getRight() + AndroidUtilities.dp(12.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f) * this.unreadProgress, this.paint);
        }
        if (this.needDivider) {
            canvas.drawLine(0.0f, 0.0f, getWidth(), 0.0f, Theme.dividerPaint);
        }
    }

    public void updateColors() {
        if (this.canAddRemove) {
            this.addButton.setProgressColor(getThemedColor(Theme.key_featuredStickers_buttonProgress));
            this.addButton.setBackgroundRoundRect(getThemedColor(Theme.key_featuredStickers_addButton), getThemedColor(Theme.key_featuredStickers_addButtonPressed));
        }
        updateStickerSetNameSearchSpan();
        updateUrlSearchSpan();
    }

    public static void createThemeDescriptions(List<ThemeDescription> descriptions, RecyclerListView listView, ThemeDescription.ThemeDescriptionDelegate delegate) {
        descriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetInfoCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_emojiPanelTrendingTitle));
        descriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetInfoCell.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_emojiPanelTrendingDescription));
        descriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetInfoCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_buttonText));
        descriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetInfoCell.class}, new String[]{"delButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_removeButtonText));
        descriptions.add(new ThemeDescription(listView, 0, new Class[]{FeaturedStickerSetInfoCell.class}, null, null, null, Theme.key_featuredStickers_unread));
        descriptions.add(new ThemeDescription(listView, 0, new Class[]{FeaturedStickerSetInfoCell.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_featuredStickers_buttonProgress));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_featuredStickers_addButton));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_featuredStickers_addButtonPressed));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_windowBackgroundWhiteBlueText4));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_chat_emojiPanelTrendingDescription));
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
