package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import com.google.android.exoplayer2.C;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderline;
/* loaded from: classes4.dex */
public class BotHelpCell extends View {
    private boolean animating;
    private String currentPhotoKey;
    private BotHelpCellDelegate delegate;
    private int height;
    private ImageReceiver imageReceiver;
    private boolean isPhotoVisible;
    private boolean isTextVisible;
    private String oldText;
    private int photoHeight;
    private ClickableSpan pressedLink;
    private Theme.ResourcesProvider resourcesProvider;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    public boolean wasDraw;
    private int width;
    private LinkPath urlPath = new LinkPath();
    private int imagePadding = AndroidUtilities.dp(4.0f);

    /* loaded from: classes4.dex */
    public interface BotHelpCellDelegate {
        void didPressUrl(String str);
    }

    public BotHelpCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.imageReceiver = imageReceiver;
        imageReceiver.setInvalidateAll(true);
        this.imageReceiver.setCrossfadeWithOldImage(true);
        this.imageReceiver.setCrossfadeDuration(300);
    }

    public void setDelegate(BotHelpCellDelegate botHelpCellDelegate) {
        this.delegate = botHelpCellDelegate;
    }

    private void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        invalidate();
    }

    public void setText(boolean bot, String text) {
        setText(bot, text, null, null);
    }

    public void setText(boolean bot, String text, TLObject imageOrAnimation, TLRPC.BotInfo botInfo) {
        String text2;
        int maxWidth;
        boolean photoVisible = imageOrAnimation != null;
        boolean textVisible = !TextUtils.isEmpty(text);
        if ((text == null || text.length() == 0) && !photoVisible) {
            setVisibility(8);
            return;
        }
        if (text != null) {
            text2 = text;
        } else {
            text2 = "";
        }
        if (text2 != null && text2.equals(this.oldText) && this.isPhotoVisible == photoVisible) {
            return;
        }
        this.isPhotoVisible = photoVisible;
        this.isTextVisible = textVisible;
        if (photoVisible) {
            String photoKey = FileRefController.getKeyForParentObject(botInfo);
            if (!ColorUtils$$ExternalSyntheticBackport0.m(this.currentPhotoKey, photoKey)) {
                this.currentPhotoKey = photoKey;
                if (imageOrAnimation instanceof TLRPC.TL_photo) {
                    TLRPC.Photo photo = (TLRPC.Photo) imageOrAnimation;
                    this.imageReceiver.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 400), photo), "400_400", null, "jpg", botInfo, 0);
                } else if (imageOrAnimation instanceof TLRPC.Document) {
                    TLRPC.Document doc = (TLRPC.Document) imageOrAnimation;
                    TLRPC.PhotoSize photoThumb = FileLoader.getClosestPhotoSizeWithSize(doc.thumbs, 400);
                    BitmapDrawable strippedThumb = null;
                    if (SharedConfig.getDevicePerformanceClass() != 0) {
                        Iterator<TLRPC.PhotoSize> it = doc.thumbs.iterator();
                        while (it.hasNext()) {
                            TLRPC.PhotoSize photoSize = it.next();
                            if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                                strippedThumb = new BitmapDrawable(getResources(), ImageLoader.getStrippedPhotoBitmap(photoSize.bytes, "b"));
                            }
                        }
                    }
                    this.imageReceiver.setImage(ImageLocation.getForDocument(doc), ImageLoader.AUTOPLAY_FILTER, ImageLocation.getForDocument(MessageObject.getDocumentVideoThumb(doc), doc), null, ImageLocation.getForDocument(photoThumb, doc), "86_86_b", strippedThumb, doc.size, "mp4", botInfo, 0);
                }
                int topRadius = AndroidUtilities.dp(SharedConfig.bubbleRadius) - AndroidUtilities.dp(2.0f);
                int bottomRadius = AndroidUtilities.dp(4.0f);
                if (!this.isTextVisible) {
                    bottomRadius = topRadius;
                }
                this.imageReceiver.setRoundRadius(topRadius, topRadius, bottomRadius, bottomRadius);
            }
        }
        this.oldText = AndroidUtilities.getSafeString(text2);
        setVisibility(0);
        if (AndroidUtilities.isTablet()) {
            maxWidth = (int) (AndroidUtilities.getMinTabletSide() * 0.7f);
        } else {
            maxWidth = (int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.7f);
        }
        if (this.isTextVisible) {
            String[] lines = text2.split("\n");
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            String help = LocaleController.getString((int) R.string.BotInfoTitle);
            if (bot) {
                stringBuilder.append((CharSequence) help);
                stringBuilder.append((CharSequence) "\n\n");
            }
            for (int a = 0; a < lines.length; a++) {
                stringBuilder.append((CharSequence) lines[a].trim());
                if (a != lines.length - 1) {
                    stringBuilder.append((CharSequence) "\n");
                }
            }
            MessageObject.addLinks(false, stringBuilder);
            if (bot) {
                stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM)), 0, help.length(), 33);
            }
            Emoji.replaceEmoji(stringBuilder, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            try {
                StaticLayout staticLayout = new StaticLayout(stringBuilder, Theme.chat_msgTextPaint, maxWidth - (this.isPhotoVisible ? AndroidUtilities.dp(5.0f) : 0), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.textLayout = staticLayout;
                this.width = 0;
                this.height = staticLayout.getHeight() + AndroidUtilities.dp(22.0f);
                int count = this.textLayout.getLineCount();
                for (int a2 = 0; a2 < count; a2++) {
                    this.width = (int) Math.ceil(Math.max(this.width, this.textLayout.getLineWidth(a2) + this.textLayout.getLineLeft(a2)));
                }
                int a3 = this.width;
                if (a3 > maxWidth || this.isPhotoVisible) {
                    this.width = maxWidth;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (this.isPhotoVisible) {
            this.width = maxWidth;
        }
        int dp = this.width + AndroidUtilities.dp(22.0f);
        this.width = dp;
        if (this.isPhotoVisible) {
            int i = this.height;
            double d = dp;
            Double.isNaN(d);
            int i2 = (int) (d * 0.5625d);
            this.photoHeight = i2;
            this.height = i + i2 + AndroidUtilities.dp(4.0f);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        BotHelpCellDelegate botHelpCellDelegate;
        float x = event.getX();
        float y = event.getY();
        boolean result = false;
        if (this.textLayout != null) {
            if (event.getAction() == 0 || (this.pressedLink != null && event.getAction() == 1)) {
                if (event.getAction() == 0) {
                    resetPressedLink();
                    try {
                        int x2 = (int) (x - this.textX);
                        int y2 = (int) (y - this.textY);
                        int line = this.textLayout.getLineForVertical(y2);
                        int off = this.textLayout.getOffsetForHorizontal(line, x2);
                        float left = this.textLayout.getLineLeft(line);
                        if (left <= x2 && this.textLayout.getLineWidth(line) + left >= x2) {
                            Spannable buffer = (Spannable) this.textLayout.getText();
                            ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                            if (link.length != 0) {
                                resetPressedLink();
                                ClickableSpan clickableSpan = link[0];
                                this.pressedLink = clickableSpan;
                                result = true;
                                try {
                                    int start = buffer.getSpanStart(clickableSpan);
                                    this.urlPath.setCurrentLayout(this.textLayout, start, 0.0f);
                                    this.textLayout.getSelectionPath(start, buffer.getSpanEnd(this.pressedLink), this.urlPath);
                                } catch (Exception e) {
                                    FileLog.e(e);
                                }
                            } else {
                                resetPressedLink();
                            }
                        } else {
                            resetPressedLink();
                        }
                    } catch (Exception e2) {
                        resetPressedLink();
                        FileLog.e(e2);
                    }
                } else {
                    ClickableSpan clickableSpan2 = this.pressedLink;
                    if (clickableSpan2 != null) {
                        try {
                            if (clickableSpan2 instanceof URLSpanNoUnderline) {
                                String url = ((URLSpanNoUnderline) clickableSpan2).getURL();
                                if ((url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) && (botHelpCellDelegate = this.delegate) != null) {
                                    botHelpCellDelegate.didPressUrl(url);
                                }
                            } else if (clickableSpan2 instanceof URLSpan) {
                                BotHelpCellDelegate botHelpCellDelegate2 = this.delegate;
                                if (botHelpCellDelegate2 != null) {
                                    botHelpCellDelegate2.didPressUrl(((URLSpan) clickableSpan2).getURL());
                                }
                            } else {
                                clickableSpan2.onClick(this);
                            }
                        } catch (Exception e3) {
                            FileLog.e(e3);
                        }
                        resetPressedLink();
                        result = true;
                    }
                }
            } else if (event.getAction() == 3) {
                resetPressedLink();
            }
        }
        return result || super.onTouchEvent(event);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), this.height + AndroidUtilities.dp(8.0f));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int x = (getWidth() - this.width) / 2;
        int y = this.photoHeight + AndroidUtilities.dp(2.0f);
        Drawable shadowDrawable = Theme.chat_msgInMediaDrawable.getShadowDrawable();
        if (shadowDrawable != null) {
            shadowDrawable.setBounds(x, y, this.width + x, this.height + y);
            shadowDrawable.draw(canvas);
        }
        int w = AndroidUtilities.displaySize.x;
        int h = AndroidUtilities.displaySize.y;
        if (getParent() instanceof View) {
            View view = (View) getParent();
            w = view.getMeasuredWidth();
            h = view.getMeasuredHeight();
        }
        Theme.MessageDrawable drawable = (Theme.MessageDrawable) getThemedDrawable(Theme.key_drawable_msgInMedia);
        drawable.setTop((int) getY(), w, h, false, false);
        drawable.setBounds(x, 0, this.width + x, this.height);
        drawable.draw(canvas);
        ImageReceiver imageReceiver = this.imageReceiver;
        int i = this.imagePadding;
        imageReceiver.setImageCoords(x + i, i, this.width - (i * 2), this.photoHeight - i);
        this.imageReceiver.draw(canvas);
        Theme.chat_msgTextPaint.setColor(getThemedColor(Theme.key_chat_messageTextIn));
        Theme.chat_msgTextPaint.linkColor = getThemedColor(Theme.key_chat_messageLinkIn);
        canvas.save();
        int dp = AndroidUtilities.dp(this.isPhotoVisible ? 14.0f : 11.0f) + x;
        this.textX = dp;
        int dp2 = AndroidUtilities.dp(11.0f) + y;
        this.textY = dp2;
        canvas.translate(dp, dp2);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.chat_urlPaint);
        }
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            staticLayout.draw(canvas);
        }
        canvas.restore();
        this.wasDraw = true;
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
        this.wasDraw = false;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setText(this.textLayout.getText());
    }

    public boolean animating() {
        return this.animating;
    }

    public void setAnimating(boolean animating) {
        this.animating = animating;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private Drawable getThemedDrawable(String drawableKey) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Drawable drawable = resourcesProvider != null ? resourcesProvider.getDrawable(drawableKey) : null;
        return drawable != null ? drawable : Theme.getThemeDrawable(drawableKey);
    }
}
