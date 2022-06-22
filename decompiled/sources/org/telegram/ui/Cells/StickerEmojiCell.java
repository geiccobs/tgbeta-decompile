package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumLockIconView;
/* loaded from: classes3.dex */
public class StickerEmojiCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private boolean changingAlpha;
    private String currentEmoji;
    private TextView emojiTextView;
    private boolean fromEmojiPanel;
    private BackupImageView imageView;
    private boolean isPremiumSticker;
    private long lastUpdateTime;
    private Object parentObject;
    private PremiumLockIconView premiumIconView;
    private boolean recent;
    private float scale;
    private boolean scaled;
    private boolean showPremiumLock;
    private TLRPC$Document sticker;
    private SendMessagesHelper.ImportingSticker stickerPath;
    private long time;
    private float alpha = 1.0f;
    private int currentAccount = UserConfig.selectedAccount;
    private float premiumAlpha = 1.0f;

    public StickerEmojiCell(Context context, boolean z) {
        super(context);
        this.fromEmojiPanel = z;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(66, 66, 17));
        TextView textView = new TextView(context);
        this.emojiTextView = textView;
        textView.setTextSize(1, 16.0f);
        new Paint(1).setColor(Theme.getColor("featuredStickers_addButton"));
        PremiumLockIconView premiumLockIconView = new PremiumLockIconView(context, PremiumLockIconView.TYPE_STICKERS_PREMIUM_LOCKED);
        this.premiumIconView = premiumLockIconView;
        premiumLockIconView.setImageReceiver(this.imageView.getImageReceiver());
        this.premiumIconView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        this.premiumIconView.setImageReceiver(this.imageView.getImageReceiver());
        addView(this.premiumIconView, LayoutHelper.createFrame(24, 24.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
        setFocusable(true);
    }

    public TLRPC$Document getSticker() {
        return this.sticker;
    }

    public SendMessagesHelper.ImportingSticker getStickerPath() {
        SendMessagesHelper.ImportingSticker importingSticker = this.stickerPath;
        if (importingSticker == null || !importingSticker.validated) {
            return null;
        }
        return importingSticker;
    }

    public String getEmoji() {
        return this.currentEmoji;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public boolean isRecent() {
        return this.recent;
    }

    public void setRecent(boolean z) {
        this.recent = z;
    }

    public void setSticker(TLRPC$Document tLRPC$Document, Object obj, boolean z) {
        setSticker(tLRPC$Document, null, obj, null, z);
    }

    public void setSticker(SendMessagesHelper.ImportingSticker importingSticker) {
        String str = importingSticker.emoji;
        setSticker(null, importingSticker, null, str, str != null);
    }

    public MessageObject.SendAnimationData getSendAnimationData() {
        ImageReceiver imageReceiver = this.imageView.getImageReceiver();
        if (!imageReceiver.hasNotThumb()) {
            return null;
        }
        MessageObject.SendAnimationData sendAnimationData = new MessageObject.SendAnimationData();
        int[] iArr = new int[2];
        this.imageView.getLocationInWindow(iArr);
        sendAnimationData.x = imageReceiver.getCenterX() + iArr[0];
        sendAnimationData.y = imageReceiver.getCenterY() + iArr[1];
        sendAnimationData.width = imageReceiver.getImageWidth();
        sendAnimationData.height = imageReceiver.getImageHeight();
        return sendAnimationData;
    }

    public void setSticker(TLRPC$Document tLRPC$Document, SendMessagesHelper.ImportingSticker importingSticker, Object obj, String str, boolean z) {
        boolean z2;
        this.currentEmoji = str;
        boolean isPremiumSticker = MessageObject.isPremiumSticker(tLRPC$Document);
        this.isPremiumSticker = isPremiumSticker;
        if (isPremiumSticker) {
            this.premiumIconView.setColor(Theme.getColor("windowBackgroundWhite"));
            this.premiumIconView.setWaitingImage();
        }
        TLRPC$PhotoSize tLRPC$PhotoSize = null;
        float f = 1.0f;
        if (importingSticker != null) {
            this.stickerPath = importingSticker;
            if (importingSticker.validated) {
                this.imageView.setImage(ImageLocation.getForPath(importingSticker.path), "80_80", null, null, DocumentObject.getSvgRectThumb("dialogBackgroundGray", 1.0f), null, importingSticker.animated ? "tgs" : null, 0, null);
            } else {
                this.imageView.setImage(null, null, null, null, DocumentObject.getSvgRectThumb("dialogBackgroundGray", 1.0f), null, importingSticker.animated ? "tgs" : null, 0, null);
            }
            if (str != null) {
                TextView textView = this.emojiTextView;
                textView.setText(Emoji.replaceEmoji(str, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                this.emojiTextView.setVisibility(0);
            } else {
                this.emojiTextView.setVisibility(4);
            }
        } else if (tLRPC$Document != null) {
            this.sticker = tLRPC$Document;
            this.parentObject = obj;
            if (!MessageObject.isVideoSticker(tLRPC$Document) || !MessageObject.canAutoplayAnimatedSticker(tLRPC$Document)) {
                tLRPC$PhotoSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90);
            }
            boolean z3 = this.fromEmojiPanel;
            String str2 = z3 ? "emptyListPlaceholder" : "windowBackgroundGray";
            if (z3) {
                f = 0.2f;
            }
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$Document, str2, f);
            if (MessageObject.canAutoplayAnimatedSticker(tLRPC$Document)) {
                if (svgThumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "66_66", (String) null, svgThumb, this.parentObject);
                } else if (tLRPC$PhotoSize != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "66_66", ImageLocation.getForDocument(tLRPC$PhotoSize, tLRPC$Document), (String) null, 0, this.parentObject);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "66_66", (String) null, (Drawable) null, this.parentObject);
                }
            } else if (svgThumb != null) {
                if (tLRPC$PhotoSize != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$PhotoSize, tLRPC$Document), "66_66", "webp", svgThumb, this.parentObject);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "66_66", "webp", svgThumb, this.parentObject);
                }
            } else if (tLRPC$PhotoSize != null) {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$PhotoSize, tLRPC$Document), "66_66", "webp", (Drawable) null, this.parentObject);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "66_66", "webp", (Drawable) null, this.parentObject);
            }
            if (str != null) {
                TextView textView2 = this.emojiTextView;
                textView2.setText(Emoji.replaceEmoji(str, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                this.emojiTextView.setVisibility(0);
            } else if (z) {
                int i = 0;
                while (true) {
                    if (i >= tLRPC$Document.attributes.size()) {
                        break;
                    }
                    TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                    if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                        String str3 = tLRPC$DocumentAttribute.alt;
                        if (str3 != null && str3.length() > 0) {
                            TextView textView3 = this.emojiTextView;
                            textView3.setText(Emoji.replaceEmoji(tLRPC$DocumentAttribute.alt, textView3.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                            z2 = true;
                        }
                    } else {
                        i++;
                    }
                }
                z2 = false;
                if (!z2) {
                    this.emojiTextView.setText(Emoji.replaceEmoji(MediaDataController.getInstance(this.currentAccount).getEmojiForSticker(this.sticker.id), this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                }
                this.emojiTextView.setVisibility(0);
            } else {
                this.emojiTextView.setVisibility(4);
            }
        }
        updatePremiumStatus(false);
        this.imageView.getImageReceiver().setAlpha(this.alpha * this.premiumAlpha);
    }

    private void updatePremiumStatus(boolean z) {
        if (this.isPremiumSticker) {
            this.showPremiumLock = true;
        } else {
            this.showPremiumLock = false;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.premiumIconView.getLayoutParams();
        if (!UserConfig.getInstance(this.currentAccount).isPremium()) {
            int dp = AndroidUtilities.dp(24.0f);
            layoutParams.width = dp;
            layoutParams.height = dp;
            layoutParams.gravity = 81;
            layoutParams.rightMargin = 0;
            layoutParams.bottomMargin = 0;
            this.premiumIconView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        } else {
            int dp2 = AndroidUtilities.dp(16.0f);
            layoutParams.width = dp2;
            layoutParams.height = dp2;
            layoutParams.gravity = 85;
            layoutParams.bottomMargin = AndroidUtilities.dp(8.0f);
            layoutParams.rightMargin = AndroidUtilities.dp(8.0f);
            this.premiumIconView.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
        }
        this.premiumIconView.setLocked(true ^ UserConfig.getInstance(this.currentAccount).isPremium());
        AndroidUtilities.updateViewVisibilityAnimated(this.premiumIconView, this.showPremiumLock, 0.9f, z);
        invalidate();
    }

    public void disable() {
        this.changingAlpha = true;
        this.alpha = 0.5f;
        this.time = 0L;
        this.imageView.getImageReceiver().setAlpha(this.alpha * this.premiumAlpha);
        this.imageView.invalidate();
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setScaled(boolean z) {
        this.scaled = z;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean isDisabled() {
        return this.changingAlpha;
    }

    public boolean showingBitmap() {
        return this.imageView.getImageReceiver().getBitmap() != null;
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    @Override // android.view.View
    public void invalidate() {
        this.emojiTextView.invalidate();
        super.invalidate();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        this.imageView.getImageReceiver().setAlpha(this.alpha * this.premiumAlpha);
        super.dispatchDraw(canvas);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View view, long j) {
        boolean z;
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView && (this.changingAlpha || (((z = this.scaled) && this.scale != 0.8f) || (!z && this.scale != 1.0f)))) {
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            if (this.changingAlpha) {
                long j3 = this.time + j2;
                this.time = j3;
                if (j3 > 1050) {
                    this.time = 1050L;
                }
                float interpolation = (interpolator.getInterpolation(((float) this.time) / 150.0f) * 0.5f) + 0.5f;
                this.alpha = interpolation;
                if (interpolation >= 1.0f) {
                    this.changingAlpha = false;
                    this.alpha = 1.0f;
                }
                this.imageView.getImageReceiver().setAlpha(this.alpha * this.premiumAlpha);
            } else {
                if (this.scaled) {
                    float f = this.scale;
                    if (f != 0.8f) {
                        float f2 = f - (((float) j2) / 400.0f);
                        this.scale = f2;
                        if (f2 < 0.8f) {
                            this.scale = 0.8f;
                        }
                    }
                }
                float f3 = this.scale + (((float) j2) / 400.0f);
                this.scale = f3;
                if (f3 > 1.0f) {
                    this.scale = 1.0f;
                }
            }
            this.imageView.setScaleX(this.scale);
            this.imageView.setScaleY(this.scale);
            this.imageView.invalidate();
            invalidate();
        }
        return drawChild;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        String string = LocaleController.getString("AttachSticker", R.string.AttachSticker);
        if (this.sticker != null) {
            int i = 0;
            while (true) {
                if (i >= this.sticker.attributes.size()) {
                    break;
                }
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.sticker.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                    String str = tLRPC$DocumentAttribute.alt;
                    if (str != null && str.length() > 0) {
                        TextView textView = this.emojiTextView;
                        textView.setText(Emoji.replaceEmoji(tLRPC$DocumentAttribute.alt, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                        string = tLRPC$DocumentAttribute.alt + " " + string;
                    }
                } else {
                    i++;
                }
            }
        }
        accessibilityNodeInfo.setContentDescription(string);
        accessibilityNodeInfo.setEnabled(true);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            updatePremiumStatus(true);
        }
    }
}
