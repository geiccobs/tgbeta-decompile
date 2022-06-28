package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
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
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumLockIconView;
/* loaded from: classes4.dex */
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
    private TLRPC.Document sticker;
    int stickerColor;
    private SendMessagesHelper.ImportingSticker stickerPath;
    private long time;
    private float alpha = 1.0f;
    private int currentAccount = UserConfig.selectedAccount;
    private float premiumAlpha = 1.0f;

    public StickerEmojiCell(Context context, boolean isEmojiPanel) {
        super(context);
        this.fromEmojiPanel = isEmojiPanel;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(66, 66, 17));
        TextView textView = new TextView(context);
        this.emojiTextView = textView;
        textView.setTextSize(1, 16.0f);
        Paint paint = new Paint(1);
        paint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
        PremiumLockIconView premiumLockIconView = new PremiumLockIconView(context, PremiumLockIconView.TYPE_STICKERS_PREMIUM_LOCKED);
        this.premiumIconView = premiumLockIconView;
        premiumLockIconView.setImageReceiver(this.imageView.getImageReceiver());
        this.premiumIconView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        this.premiumIconView.setImageReceiver(this.imageView.getImageReceiver());
        addView(this.premiumIconView, LayoutHelper.createFrame(24, 24.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
        setFocusable(true);
    }

    public TLRPC.Document getSticker() {
        return this.sticker;
    }

    public SendMessagesHelper.ImportingSticker getStickerPath() {
        SendMessagesHelper.ImportingSticker importingSticker = this.stickerPath;
        if (importingSticker == null || !importingSticker.validated) {
            return null;
        }
        return this.stickerPath;
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

    public void setRecent(boolean value) {
        this.recent = value;
    }

    public void setSticker(TLRPC.Document document, Object parent, boolean showEmoji) {
        setSticker(document, null, parent, null, showEmoji);
    }

    public void setSticker(SendMessagesHelper.ImportingSticker path) {
        setSticker(null, path, null, path.emoji, path.emoji != null);
    }

    public MessageObject.SendAnimationData getSendAnimationData() {
        ImageReceiver imageReceiver = this.imageView.getImageReceiver();
        if (!imageReceiver.hasNotThumb()) {
            return null;
        }
        MessageObject.SendAnimationData data = new MessageObject.SendAnimationData();
        int[] position = new int[2];
        this.imageView.getLocationInWindow(position);
        data.x = imageReceiver.getCenterX() + position[0];
        data.y = imageReceiver.getCenterY() + position[1];
        data.width = imageReceiver.getImageWidth();
        data.height = imageReceiver.getImageHeight();
        return data;
    }

    public void setSticker(TLRPC.Document document, SendMessagesHelper.ImportingSticker path, Object parent, String emoji, boolean showEmoji) {
        TLRPC.Document document2 = document;
        this.currentEmoji = emoji;
        boolean isPremiumSticker = MessageObject.isPremiumSticker(document);
        this.isPremiumSticker = isPremiumSticker;
        if (isPremiumSticker) {
            this.premiumIconView.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.premiumIconView.setWaitingImage();
        }
        TLRPC.PhotoSize thumb = null;
        float f = 1.0f;
        if (path != null) {
            this.stickerPath = path;
            if (path.validated) {
                this.imageView.setImage(ImageLocation.getForPath(path.path), "80_80", null, null, DocumentObject.getSvgRectThumb(Theme.key_dialogBackgroundGray, 1.0f), null, path.animated ? "tgs" : null, 0, null);
            } else {
                this.imageView.setImage(null, null, null, null, DocumentObject.getSvgRectThumb(Theme.key_dialogBackgroundGray, 1.0f), null, path.animated ? "tgs" : null, 0, null);
            }
            if (emoji == null) {
                this.emojiTextView.setVisibility(4);
            } else {
                TextView textView = this.emojiTextView;
                textView.setText(Emoji.replaceEmoji(emoji, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                this.emojiTextView.setVisibility(0);
            }
        } else if (document2 != null) {
            this.sticker = document2;
            this.parentObject = parent;
            boolean isVideoSticker = MessageObject.isVideoSticker(document);
            if (!isVideoSticker || !MessageObject.canAutoplayAnimatedSticker(document)) {
                thumb = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
            }
            boolean z = this.fromEmojiPanel;
            String str = z ? Theme.key_emptyListPlaceholder : Theme.key_windowBackgroundGray;
            if (z) {
                f = 0.2f;
            }
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(document2, str, f);
            if (MessageObject.canAutoplayAnimatedSticker(document)) {
                if (svgThumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(document), "66_66", (String) null, svgThumb, this.parentObject);
                } else if (thumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(document), "66_66", ImageLocation.getForDocument(thumb, document2), (String) null, 0, this.parentObject);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(document), "66_66", (String) null, (Drawable) null, this.parentObject);
                }
            } else if (svgThumb != null) {
                if (thumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(thumb, document2), "66_66", "webp", svgThumb, this.parentObject);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(document), "66_66", "webp", svgThumb, this.parentObject);
                }
            } else if (thumb != null) {
                this.imageView.setImage(ImageLocation.getForDocument(thumb, document2), "66_66", "webp", (Drawable) null, this.parentObject);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(document), "66_66", "webp", (Drawable) null, this.parentObject);
            }
            if (emoji != null) {
                TextView textView2 = this.emojiTextView;
                textView2.setText(Emoji.replaceEmoji(emoji, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                this.emojiTextView.setVisibility(0);
            } else if (!showEmoji) {
                this.emojiTextView.setVisibility(4);
            } else {
                boolean set = false;
                int a = 0;
                while (true) {
                    if (a >= document2.attributes.size()) {
                        break;
                    }
                    TLRPC.DocumentAttribute attribute = document2.attributes.get(a);
                    if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                        a++;
                        document2 = document;
                    } else if (attribute.alt != null && attribute.alt.length() > 0) {
                        this.emojiTextView.setText(Emoji.replaceEmoji(attribute.alt, this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                        set = true;
                    }
                }
                if (!set) {
                    this.emojiTextView.setText(Emoji.replaceEmoji(MediaDataController.getInstance(this.currentAccount).getEmojiForSticker(this.sticker.id), this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                }
                this.emojiTextView.setVisibility(0);
            }
        }
        updatePremiumStatus(false);
        this.imageView.getImageReceiver().setAlpha(this.alpha * this.premiumAlpha);
    }

    private void updatePremiumStatus(boolean animated) {
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
        AndroidUtilities.updateViewVisibilityAnimated(this.premiumIconView, this.showPremiumLock, 0.9f, animated);
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

    public void setScaled(boolean value) {
        this.scaled = value;
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
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean z;
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.imageView && (this.changingAlpha || (((z = this.scaled) && this.scale != 0.8f) || (!z && this.scale != 1.0f)))) {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            if (this.changingAlpha) {
                long j = this.time + dt;
                this.time = j;
                if (j > 1050) {
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
                        float f2 = f - (((float) dt) / 400.0f);
                        this.scale = f2;
                        if (f2 < 0.8f) {
                            this.scale = 0.8f;
                        }
                    }
                }
                float f3 = this.scale + (((float) dt) / 400.0f);
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
        return result;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        String descr = LocaleController.getString("AttachSticker", R.string.AttachSticker);
        if (this.sticker != null) {
            int a = 0;
            while (true) {
                if (a >= this.sticker.attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute attribute = this.sticker.attributes.get(a);
                if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                    a++;
                } else if (attribute.alt != null && attribute.alt.length() > 0) {
                    this.emojiTextView.setText(Emoji.replaceEmoji(attribute.alt, this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                    descr = attribute.alt + " " + descr;
                }
            }
        }
        info.setContentDescription(descr);
        info.setEnabled(true);
    }

    public void showRequirePremiumAnimation() {
        if (this.premiumIconView != null) {
            Vibrator v = (Vibrator) getContext().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200L);
            }
            AndroidUtilities.shakeView(this.premiumIconView, 2.0f, 0);
        }
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
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.currentUserPremiumStatusChanged) {
            updatePremiumStatus(true);
        }
    }
}
