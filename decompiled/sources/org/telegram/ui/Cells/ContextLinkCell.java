package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes4.dex */
public class ContextLinkCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GEO = 8;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_PHOTO = 7;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private static int resolveFileIdPointer;
    public final Property<ContextLinkCell, Float> IMAGE_SCALE;
    private int TAG;
    private AnimatorSet animator;
    private Paint backgroundPaint;
    private boolean buttonPressed;
    private int buttonState;
    File cacheFile;
    private boolean canPreviewGif;
    private CheckBox2 checkBox;
    private int currentAccount;
    private int currentDate;
    private MessageObject currentMessageObject;
    private TLRPC.PhotoSize currentPhotoObject;
    private ContextLinkCellDelegate delegate;
    private StaticLayout descriptionLayout;
    private int descriptionY;
    private TLRPC.Document documentAttach;
    private int documentAttachType;
    private boolean drawLinkImageView;
    boolean fileExist;
    String fileName;
    private boolean hideLoadProgress;
    private float imageScale;
    private TLRPC.User inlineBot;
    private TLRPC.BotInlineResult inlineResult;
    private boolean isForceGif;
    private long lastUpdateTime;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private StaticLayout linkLayout;
    private int linkY;
    private boolean mediaWebpage;
    private boolean needDivider;
    private boolean needShadow;
    private Object parentObject;
    private TLRPC.Photo photoAttach;
    private RadialProgress2 radialProgress;
    int resolveFileNameId;
    boolean resolvingFileName;
    private Theme.ResourcesProvider resourcesProvider;
    private float scale;
    private boolean scaled;
    private StaticLayout titleLayout;
    private int titleY;

    /* loaded from: classes4.dex */
    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    public ContextLinkCell(Context context) {
        this(context, false, null);
    }

    public ContextLinkCell(Context context, boolean needsCheckBox) {
        this(context, needsCheckBox, null);
    }

    public ContextLinkCell(Context context, boolean needsCheckBox, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.titleY = AndroidUtilities.dp(7.0f);
        this.descriptionY = AndroidUtilities.dp(27.0f);
        this.cacheFile = null;
        this.imageScale = 1.0f;
        this.IMAGE_SCALE = new AnimationProperties.FloatProperty<ContextLinkCell>("animationValue") { // from class: org.telegram.ui.Cells.ContextLinkCell.2
            public void setValue(ContextLinkCell object, float value) {
                ContextLinkCell.this.imageScale = value;
                ContextLinkCell.this.invalidate();
            }

            public Float get(ContextLinkCell object) {
                return Float.valueOf(ContextLinkCell.this.imageScale);
            }
        };
        this.resourcesProvider = resourcesProvider;
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.linkImageView = imageReceiver;
        imageReceiver.setLayerNum(1);
        this.linkImageView.setUseSharedAnimationQueue(true);
        this.letterDrawable = new LetterDrawable(resourcesProvider);
        this.radialProgress = new RadialProgress2(this);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        setFocusable(true);
        if (needsCheckBox) {
            Paint paint = new Paint();
            this.backgroundPaint = paint;
            paint.setColor(Theme.getColor(Theme.key_sharedMedia_photoPlaceholder, resourcesProvider));
            CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(null, Theme.key_sharedMedia_photoPlaceholder, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(1);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 53, 0.0f, 1.0f, 1.0f, 0.0f));
        }
        setWillNotDraw(false);
    }

    /* JADX WARN: Removed duplicated region for block: B:117:0x0302  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x030b  */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0332 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:134:0x033c  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x034f  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x0362 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:148:0x0373  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x0381  */
    /* JADX WARN: Removed duplicated region for block: B:158:0x03e1  */
    /* JADX WARN: Removed duplicated region for block: B:161:0x03eb  */
    /* JADX WARN: Removed duplicated region for block: B:164:0x03f3  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x04a0  */
    /* JADX WARN: Removed duplicated region for block: B:196:0x05c9  */
    /* JADX WARN: Removed duplicated region for block: B:200:0x060a  */
    /* JADX WARN: Removed duplicated region for block: B:226:0x06e2  */
    /* JADX WARN: Removed duplicated region for block: B:227:0x06f2  */
    /* JADX WARN: Removed duplicated region for block: B:231:0x014a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0190  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x01a3  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x01eb  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0210  */
    /* JADX WARN: Type inference failed for: r1v1, types: [boolean] */
    /* JADX WARN: Type inference failed for: r1v22 */
    /* JADX WARN: Type inference failed for: r1v37 */
    /* JADX WARN: Type inference failed for: r1v38 */
    /* JADX WARN: Type inference failed for: r1v39 */
    @Override // android.widget.FrameLayout, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onMeasure(int r37, int r38) {
        /*
            Method dump skipped, instructions count: 1782
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.onMeasure(int, int):void");
    }

    private void setAttachType() {
        this.currentMessageObject = null;
        this.documentAttachType = 0;
        TLRPC.Document document = this.documentAttach;
        if (document != null) {
            if (MessageObject.isGifDocument(document)) {
                this.documentAttachType = 2;
            } else if (MessageObject.isStickerDocument(this.documentAttach) || MessageObject.isAnimatedStickerDocument(this.documentAttach, true)) {
                this.documentAttachType = 6;
            } else if (MessageObject.isMusicDocument(this.documentAttach)) {
                this.documentAttachType = 5;
            } else if (MessageObject.isVoiceDocument(this.documentAttach)) {
                this.documentAttachType = 3;
            }
        } else {
            TLRPC.BotInlineResult botInlineResult = this.inlineResult;
            if (botInlineResult != null) {
                if (botInlineResult.photo != null) {
                    this.documentAttachType = 7;
                } else if (this.inlineResult.type.equals("audio")) {
                    this.documentAttachType = 5;
                } else if (this.inlineResult.type.equals("voice")) {
                    this.documentAttachType = 3;
                }
            }
        }
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            TLRPC.TL_message message = new TLRPC.TL_message();
            message.out = true;
            message.id = -Utilities.random.nextInt();
            message.peer_id = new TLRPC.TL_peerUser();
            message.from_id = new TLRPC.TL_peerUser();
            TLRPC.Peer peer = message.peer_id;
            TLRPC.Peer peer2 = message.from_id;
            long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            peer2.user_id = clientUserId;
            peer.user_id = clientUserId;
            message.date = (int) (System.currentTimeMillis() / 1000);
            String str = "";
            message.message = str;
            message.media = new TLRPC.TL_messageMediaDocument();
            message.media.flags |= 3;
            message.media.document = new TLRPC.TL_document();
            message.media.document.file_reference = new byte[0];
            message.flags |= 768;
            if (this.documentAttach != null) {
                message.media.document = this.documentAttach;
                message.attachPath = str;
            } else {
                String str2 = "mp3";
                String ext = ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? str2 : "ogg");
                message.media.document.id = 0L;
                message.media.document.access_hash = 0L;
                message.media.document.date = message.date;
                message.media.document.mime_type = "audio/" + ext;
                message.media.document.size = 0L;
                message.media.document.dc_id = 0;
                TLRPC.TL_documentAttributeAudio attributeAudio = new TLRPC.TL_documentAttributeAudio();
                attributeAudio.duration = MessageObject.getInlineResultDuration(this.inlineResult);
                attributeAudio.title = this.inlineResult.title != null ? this.inlineResult.title : str;
                if (this.inlineResult.description != null) {
                    str = this.inlineResult.description;
                }
                attributeAudio.performer = str;
                attributeAudio.flags |= 3;
                if (this.documentAttachType == 3) {
                    attributeAudio.voice = true;
                }
                message.media.document.attributes.add(attributeAudio);
                TLRPC.TL_documentAttributeFilename fileName = new TLRPC.TL_documentAttributeFilename();
                StringBuilder sb = new StringBuilder();
                sb.append(Utilities.MD5(this.inlineResult.content.url));
                sb.append(".");
                sb.append(ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? str2 : "ogg"));
                fileName.file_name = sb.toString();
                message.media.document.attributes.add(fileName);
                File directory = FileLoader.getDirectory(4);
                StringBuilder sb2 = new StringBuilder();
                sb2.append(Utilities.MD5(this.inlineResult.content.url));
                sb2.append(".");
                String str3 = this.inlineResult.content.url;
                if (this.documentAttachType != 5) {
                    str2 = "ogg";
                }
                sb2.append(ImageLoader.getHttpUrlExtension(str3, str2));
                message.attachPath = new File(directory, sb2.toString()).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(this.currentAccount, message, false, true);
        }
    }

    public void setLink(TLRPC.BotInlineResult contextResult, TLRPC.User bot, boolean media, boolean divider, boolean shadow) {
        setLink(contextResult, bot, media, divider, shadow, false);
    }

    public void setLink(TLRPC.BotInlineResult contextResult, TLRPC.User bot, boolean media, boolean divider, boolean shadow, boolean forceGif) {
        this.needDivider = divider;
        this.needShadow = shadow;
        this.inlineBot = bot;
        this.inlineResult = contextResult;
        this.parentObject = contextResult;
        if (contextResult != null) {
            this.documentAttach = contextResult.document;
            this.photoAttach = this.inlineResult.photo;
        } else {
            this.documentAttach = null;
            this.photoAttach = null;
        }
        this.mediaWebpage = media;
        this.isForceGif = forceGif;
        setAttachType();
        if (forceGif) {
            this.documentAttachType = 2;
        }
        requestLayout();
        this.fileName = null;
        this.cacheFile = null;
        this.fileExist = false;
        this.resolvingFileName = false;
        updateButtonState(false, false);
    }

    public TLRPC.User getInlineBot() {
        return this.inlineBot;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public void setGif(TLRPC.Document document, boolean divider) {
        setGif(document, "gif" + document, 0, divider);
    }

    public void setGif(TLRPC.Document document, Object parent, int date, boolean divider) {
        this.needDivider = divider;
        this.needShadow = false;
        this.currentDate = date;
        this.inlineResult = null;
        this.parentObject = parent;
        this.documentAttach = document;
        this.photoAttach = null;
        this.mediaWebpage = true;
        this.isForceGif = true;
        setAttachType();
        this.documentAttachType = 2;
        requestLayout();
        this.fileName = null;
        this.cacheFile = null;
        this.fileExist = false;
        this.resolvingFileName = false;
        updateButtonState(false, false);
    }

    public boolean isSticker() {
        return this.documentAttachType == 6;
    }

    public boolean isGif() {
        return this.documentAttachType == 2 && this.canPreviewGif;
    }

    public boolean showingBitmap() {
        return this.linkImageView.getBitmap() != null;
    }

    public int getDate() {
        return this.currentDate;
    }

    public TLRPC.Document getDocument() {
        return this.documentAttach;
    }

    public TLRPC.BotInlineResult getBotInlineResult() {
        return this.inlineResult;
    }

    public ImageReceiver getPhotoImage() {
        return this.linkImageView;
    }

    public void setScaled(boolean value) {
        this.scaled = value;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setCanPreviewGif(boolean value) {
        this.canPreviewGif = value;
    }

    public boolean isCanPreviewGif() {
        return this.canPreviewGif;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.linkImageView.onDetachedFromWindow();
        this.radialProgress.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.linkImageView.onAttachedToWindow()) {
            updateButtonState(false, false);
        }
        this.radialProgress.onAttachedToWindow();
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mediaWebpage || this.delegate == null || this.inlineResult == null) {
            return super.onTouchEvent(event);
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean result = false;
        AndroidUtilities.dp(48.0f);
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            boolean area = this.letterDrawable.getBounds().contains(x, y);
            if (event.getAction() == 0) {
                if (area) {
                    this.buttonPressed = true;
                    this.radialProgress.setPressed(true, false);
                    invalidate();
                    result = true;
                }
            } else if (this.buttonPressed) {
                if (event.getAction() == 1) {
                    this.buttonPressed = false;
                    playSoundEffect(0);
                    didPressedButton();
                    invalidate();
                } else if (event.getAction() == 3) {
                    this.buttonPressed = false;
                    invalidate();
                } else if (event.getAction() == 2 && !area) {
                    this.buttonPressed = false;
                    invalidate();
                }
                this.radialProgress.setPressed(this.buttonPressed, false);
            }
        } else {
            TLRPC.BotInlineResult botInlineResult = this.inlineResult;
            if (botInlineResult != null && botInlineResult.content != null && !TextUtils.isEmpty(this.inlineResult.content.url)) {
                if (event.getAction() == 0) {
                    if (this.letterDrawable.getBounds().contains(x, y)) {
                        this.buttonPressed = true;
                        result = true;
                    }
                } else if (this.buttonPressed) {
                    if (event.getAction() == 1) {
                        this.buttonPressed = false;
                        playSoundEffect(0);
                        this.delegate.didPressedImage(this);
                    } else if (event.getAction() == 3) {
                        this.buttonPressed = false;
                    } else if (event.getAction() == 2 && !this.letterDrawable.getBounds().contains(x, y)) {
                        this.buttonPressed = false;
                    }
                }
            }
        }
        if (!result) {
            return super.onTouchEvent(event);
        }
        return result;
    }

    private void didPressedButton() {
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            int i2 = this.buttonState;
            if (i2 == 0) {
                if (MediaController.getInstance().playMessage(this.currentMessageObject)) {
                    this.buttonState = 1;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                    invalidate();
                }
            } else if (i2 == 1) {
                boolean result = MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.currentMessageObject);
                if (result) {
                    this.buttonState = 0;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                    invalidate();
                }
            } else if (i2 == 2) {
                this.radialProgress.setProgress(0.0f, false);
                if (this.documentAttach != null) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.inlineResult, 1, 0);
                } else if (this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                    FileLoader.getInstance(this.currentAccount).loadFile(WebFile.createWithWebDocument(this.inlineResult.content), 1, 1);
                }
                this.buttonState = 4;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            } else if (i2 == 4) {
                if (this.documentAttach != null) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                } else if (this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(WebFile.createWithWebDocument(this.inlineResult.content));
                }
                this.buttonState = 2;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            }
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        TLRPC.BotInlineResult botInlineResult;
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null && (checkBox2.isChecked() || !this.linkImageView.hasBitmapImage() || this.linkImageView.getCurrentAlpha() != 1.0f || PhotoViewer.isShowingImage((MessageObject) this.parentObject))) {
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.backgroundPaint);
        }
        float f = 8.0f;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate(AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline), this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourcesProvider));
            canvas.save();
            canvas.translate(AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline), this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.linkLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, this.resourcesProvider));
            canvas.save();
            if (!LocaleController.isRTL) {
                f = AndroidUtilities.leftBaseline;
            }
            canvas.translate(AndroidUtilities.dp(f), this.linkY);
            this.linkLayout.draw(canvas);
            canvas.restore();
        }
        if (!this.mediaWebpage) {
            if (this.drawLinkImageView && !PhotoViewer.isShowingImage(this.inlineResult)) {
                this.letterDrawable.setAlpha((int) ((1.0f - this.linkImageView.getCurrentAlpha()) * 255.0f));
            } else {
                this.letterDrawable.setAlpha(255);
            }
            int i2 = this.documentAttachType;
            if (i2 == 3 || i2 == 5) {
                this.radialProgress.setProgressColor(Theme.getColor(this.buttonPressed ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress, this.resourcesProvider));
                this.radialProgress.draw(canvas);
            } else {
                TLRPC.BotInlineResult botInlineResult2 = this.inlineResult;
                if (botInlineResult2 == null || !botInlineResult2.type.equals("file")) {
                    TLRPC.BotInlineResult botInlineResult3 = this.inlineResult;
                    if (botInlineResult3 == null || (!botInlineResult3.type.equals("audio") && !this.inlineResult.type.equals("voice"))) {
                        TLRPC.BotInlineResult botInlineResult4 = this.inlineResult;
                        if (botInlineResult4 != null && (botInlineResult4.type.equals("venue") || this.inlineResult.type.equals("geo"))) {
                            int w = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                            int h = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                            int x = (int) (this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2));
                            int y = (int) (this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2));
                            canvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f), this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f), LetterDrawable.paint);
                            Theme.chat_inlineResultLocation.setBounds(x, y, x + w, y + h);
                            Theme.chat_inlineResultLocation.draw(canvas);
                        } else {
                            this.letterDrawable.draw(canvas);
                        }
                    } else {
                        int w2 = Theme.chat_inlineResultAudio.getIntrinsicWidth();
                        int h2 = Theme.chat_inlineResultAudio.getIntrinsicHeight();
                        int x2 = (int) (this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w2) / 2));
                        int y2 = (int) (this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h2) / 2));
                        canvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f), this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f), LetterDrawable.paint);
                        Theme.chat_inlineResultAudio.setBounds(x2, y2, x2 + w2, y2 + h2);
                        Theme.chat_inlineResultAudio.draw(canvas);
                    }
                } else {
                    int w3 = Theme.chat_inlineResultFile.getIntrinsicWidth();
                    int h3 = Theme.chat_inlineResultFile.getIntrinsicHeight();
                    int x3 = (int) (this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w3) / 2));
                    int y3 = (int) (this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h3) / 2));
                    canvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f), this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f), LetterDrawable.paint);
                    Theme.chat_inlineResultFile.setBounds(x3, y3, x3 + w3, y3 + h3);
                    Theme.chat_inlineResultFile.draw(canvas);
                }
            }
        } else {
            TLRPC.BotInlineResult botInlineResult5 = this.inlineResult;
            if (botInlineResult5 != null && ((botInlineResult5.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo) || (this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue))) {
                int w4 = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                int h4 = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                int x4 = (int) (this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - w4) / 2.0f));
                int y4 = (int) (this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - h4) / 2.0f));
                canvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + this.linkImageView.getImageWidth(), this.linkImageView.getImageY() + this.linkImageView.getImageHeight(), LetterDrawable.paint);
                Theme.chat_inlineResultLocation.setBounds(x4, y4, x4 + w4, y4 + h4);
                Theme.chat_inlineResultLocation.draw(canvas);
            }
        }
        if (this.drawLinkImageView) {
            if (this.inlineResult != null) {
                this.linkImageView.setVisible(!PhotoViewer.isShowingImage(botInlineResult), false);
            }
            canvas.save();
            boolean z = this.scaled;
            if ((z && this.scale != 0.8f) || (!z && this.scale != 1.0f)) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                this.lastUpdateTime = newTime;
                if (this.scaled) {
                    float f2 = this.scale;
                    if (f2 != 0.8f) {
                        float f3 = f2 - (((float) dt) / 400.0f);
                        this.scale = f3;
                        if (f3 < 0.8f) {
                            this.scale = 0.8f;
                        }
                        invalidate();
                    }
                }
                float f4 = this.scale + (((float) dt) / 400.0f);
                this.scale = f4;
                if (f4 > 1.0f) {
                    this.scale = 1.0f;
                }
                invalidate();
            }
            float f5 = this.scale;
            float f6 = this.imageScale;
            canvas.scale(f5 * f6, f5 * f6, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
            this.linkImageView.draw(canvas);
            canvas.restore();
        }
        if (this.mediaWebpage && ((i = this.documentAttachType) == 7 || i == 2)) {
            this.radialProgress.draw(canvas);
        }
        if (this.needDivider && !this.mediaWebpage) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, Theme.dividerPaint);
            } else {
                canvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
        if (this.needShadow) {
            Theme.chat_contextResult_shadowUnderSwitchDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(3.0f));
            Theme.chat_contextResult_shadowUnderSwitchDrawable.draw(canvas);
        }
    }

    private int getIconForCurrentState() {
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            this.radialProgress.setColors(Theme.key_chat_inLoader, Theme.key_chat_inLoaderSelected, Theme.key_chat_inMediaIcon, Theme.key_chat_inMediaIconSelected);
            int i2 = this.buttonState;
            if (i2 == 1) {
                return 1;
            }
            if (i2 == 2) {
                return 2;
            }
            return i2 == 4 ? 3 : 0;
        }
        this.radialProgress.setColors(Theme.key_chat_mediaLoaderPhoto, Theme.key_chat_mediaLoaderPhotoSelected, Theme.key_chat_mediaLoaderPhotoIcon, Theme.key_chat_mediaLoaderPhotoIconSelected);
        return this.buttonState == 1 ? 10 : 4;
    }

    public void updateButtonState(boolean ifSame, boolean animated) {
        boolean isLoading;
        String str = this.fileName;
        if (str == null && !this.resolvingFileName) {
            this.resolvingFileName = true;
            int localId = this.resolveFileNameId;
            this.resolveFileNameId = localId + 1;
            this.resolveFileNameId = localId;
            Utilities.searchQueue.postRunnable(new AnonymousClass1(localId, ifSame));
            this.radialProgress.setIcon(4, ifSame, false);
        } else if (TextUtils.isEmpty(str)) {
            this.buttonState = -1;
            this.radialProgress.setIcon(4, ifSame, false);
        } else {
            if (this.documentAttach != null) {
                isLoading = FileLoader.getInstance(this.currentAccount).isLoadingFile(this.fileName);
            } else {
                isLoading = ImageLoader.getInstance().isLoadingHttpFile(this.fileName);
            }
            if (isLoading || !this.fileExist) {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(this.fileName, this);
                int i = this.documentAttachType;
                float f = 0.0f;
                if (i == 5 || i == 3) {
                    if (!isLoading) {
                        this.buttonState = 2;
                    } else {
                        this.buttonState = 4;
                        Float progress = ImageLoader.getInstance().getFileProgress(this.fileName);
                        if (progress != null) {
                            this.radialProgress.setProgress(progress.floatValue(), animated);
                        } else {
                            this.radialProgress.setProgress(0.0f, animated);
                        }
                    }
                } else {
                    this.buttonState = 1;
                    Float progress2 = ImageLoader.getInstance().getFileProgress(this.fileName);
                    if (progress2 != null) {
                        f = progress2.floatValue();
                    }
                    float setProgress = f;
                    this.radialProgress.setProgress(setProgress, false);
                }
            } else {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                int i2 = this.documentAttachType;
                if (i2 == 5 || i2 == 3) {
                    boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                    if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                        this.buttonState = 0;
                    } else {
                        this.buttonState = 1;
                    }
                    this.radialProgress.setProgress(1.0f, animated);
                } else {
                    this.buttonState = -1;
                }
            }
            this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
            invalidate();
        }
    }

    /* renamed from: org.telegram.ui.Cells.ContextLinkCell$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 implements Runnable {
        final /* synthetic */ boolean val$ifSame;
        final /* synthetic */ int val$localId;

        AnonymousClass1(int i, boolean z) {
            ContextLinkCell.this = this$0;
            this.val$localId = i;
            this.val$ifSame = z;
        }

        @Override // java.lang.Runnable
        public void run() {
            String fileName = null;
            File cacheFile = null;
            if (ContextLinkCell.this.documentAttachType == 5 || ContextLinkCell.this.documentAttachType == 3) {
                if (ContextLinkCell.this.documentAttach != null) {
                    fileName = FileLoader.getAttachFileName(ContextLinkCell.this.documentAttach);
                    cacheFile = FileLoader.getInstance(ContextLinkCell.this.currentAccount).getPathToAttach(ContextLinkCell.this.documentAttach);
                } else if (ContextLinkCell.this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(Utilities.MD5(ContextLinkCell.this.inlineResult.content.url));
                    sb.append(".");
                    sb.append(ImageLoader.getHttpUrlExtension(ContextLinkCell.this.inlineResult.content.url, ContextLinkCell.this.documentAttachType == 5 ? "mp3" : "ogg"));
                    fileName = sb.toString();
                    cacheFile = new File(FileLoader.getDirectory(4), fileName);
                }
            } else if (ContextLinkCell.this.mediaWebpage) {
                if (ContextLinkCell.this.inlineResult != null) {
                    if (ContextLinkCell.this.inlineResult.document instanceof TLRPC.TL_document) {
                        fileName = FileLoader.getAttachFileName(ContextLinkCell.this.inlineResult.document);
                        cacheFile = FileLoader.getInstance(ContextLinkCell.this.currentAccount).getPathToAttach(ContextLinkCell.this.inlineResult.document);
                    } else if (!(ContextLinkCell.this.inlineResult.photo instanceof TLRPC.TL_photo)) {
                        if (!(ContextLinkCell.this.inlineResult.content instanceof TLRPC.TL_webDocument)) {
                            if (ContextLinkCell.this.inlineResult.thumb instanceof TLRPC.TL_webDocument) {
                                fileName = Utilities.MD5(ContextLinkCell.this.inlineResult.thumb.url) + "." + ImageLoader.getHttpUrlExtension(ContextLinkCell.this.inlineResult.thumb.url, FileLoader.getMimeTypePart(ContextLinkCell.this.inlineResult.thumb.mime_type));
                                cacheFile = new File(FileLoader.getDirectory(4), fileName);
                            }
                        } else {
                            fileName = Utilities.MD5(ContextLinkCell.this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(ContextLinkCell.this.inlineResult.content.url, FileLoader.getMimeTypePart(ContextLinkCell.this.inlineResult.content.mime_type));
                            cacheFile = new File(FileLoader.getDirectory(4), fileName);
                            if (ContextLinkCell.this.documentAttachType == 2 && (ContextLinkCell.this.inlineResult.thumb instanceof TLRPC.TL_webDocument) && MimeTypes.VIDEO_MP4.equals(ContextLinkCell.this.inlineResult.thumb.mime_type)) {
                                fileName = null;
                            }
                        }
                    } else {
                        ContextLinkCell contextLinkCell = ContextLinkCell.this;
                        contextLinkCell.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(contextLinkCell.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
                        fileName = FileLoader.getAttachFileName(ContextLinkCell.this.currentPhotoObject);
                        cacheFile = FileLoader.getInstance(ContextLinkCell.this.currentAccount).getPathToAttach(ContextLinkCell.this.currentPhotoObject);
                    }
                } else if (ContextLinkCell.this.documentAttach != null) {
                    fileName = FileLoader.getAttachFileName(ContextLinkCell.this.documentAttach);
                    cacheFile = FileLoader.getInstance(ContextLinkCell.this.currentAccount).getPathToAttach(ContextLinkCell.this.documentAttach);
                }
                if (ContextLinkCell.this.documentAttach != null && ContextLinkCell.this.documentAttachType == 2 && MessageObject.getDocumentVideoThumb(ContextLinkCell.this.documentAttach) != null) {
                    fileName = null;
                }
            }
            final String fileNameFinal = fileName;
            final File cacheFileFinal = cacheFile;
            final boolean fileExist = !TextUtils.isEmpty(fileName) && cacheFile.exists();
            final int i = this.val$localId;
            final boolean z = this.val$ifSame;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Cells.ContextLinkCell$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ContextLinkCell.AnonymousClass1.this.m1640lambda$run$0$orgtelegramuiCellsContextLinkCell$1(i, fileNameFinal, cacheFileFinal, fileExist, z);
                }
            });
        }

        /* renamed from: lambda$run$0$org-telegram-ui-Cells-ContextLinkCell$1 */
        public /* synthetic */ void m1640lambda$run$0$orgtelegramuiCellsContextLinkCell$1(int localId, String fileNameFinal, File cacheFileFinal, boolean fileExist, boolean ifSame) {
            ContextLinkCell.this.resolvingFileName = false;
            if (ContextLinkCell.this.resolveFileNameId == localId) {
                ContextLinkCell.this.fileName = fileNameFinal;
                if (ContextLinkCell.this.fileName == null) {
                    ContextLinkCell.this.fileName = "";
                }
                ContextLinkCell.this.cacheFile = cacheFileFinal;
                ContextLinkCell.this.fileExist = fileExist;
            }
            ContextLinkCell.this.updateButtonState(ifSame, true);
        }
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public TLRPC.BotInlineResult getResult() {
        return this.inlineResult;
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onFailedDownload(String fileName, boolean canceled) {
        updateButtonState(true, canceled);
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onSuccessDownload(String fileName) {
        this.fileExist = true;
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(false, true);
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {
        this.radialProgress.setProgress(Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize)), true);
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            if (this.buttonState != 4) {
                updateButtonState(false, true);
            }
        } else if (this.buttonState != 1) {
            updateButtonState(false, true);
        }
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public int getObserverTag() {
        return this.TAG;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StringBuilder sbuf = new StringBuilder();
        switch (this.documentAttachType) {
            case 1:
                sbuf.append(LocaleController.getString("AttachDocument", R.string.AttachDocument));
                break;
            case 2:
                sbuf.append(LocaleController.getString("AttachGif", R.string.AttachGif));
                break;
            case 3:
                sbuf.append(LocaleController.getString("AttachAudio", R.string.AttachAudio));
                break;
            case 4:
                sbuf.append(LocaleController.getString("AttachVideo", R.string.AttachVideo));
                break;
            case 5:
                sbuf.append(LocaleController.getString("AttachMusic", R.string.AttachMusic));
                break;
            case 6:
                sbuf.append(LocaleController.getString("AttachSticker", R.string.AttachSticker));
                break;
            case 7:
                sbuf.append(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
                break;
            case 8:
                sbuf.append(LocaleController.getString("AttachLocation", R.string.AttachLocation));
                break;
        }
        StaticLayout staticLayout = this.titleLayout;
        boolean hasTitle = staticLayout != null && !TextUtils.isEmpty(staticLayout.getText());
        StaticLayout staticLayout2 = this.descriptionLayout;
        boolean hasDescription = staticLayout2 != null && !TextUtils.isEmpty(staticLayout2.getText());
        if (this.documentAttachType == 5 && hasTitle && hasDescription) {
            sbuf.append(", ");
            sbuf.append(LocaleController.formatString("AccDescrMusicInfo", R.string.AccDescrMusicInfo, this.descriptionLayout.getText(), this.titleLayout.getText()));
        } else {
            if (hasTitle) {
                if (sbuf.length() > 0) {
                    sbuf.append(", ");
                }
                sbuf.append(this.titleLayout.getText());
            }
            if (hasDescription) {
                if (sbuf.length() > 0) {
                    sbuf.append(", ");
                }
                sbuf.append(this.descriptionLayout.getText());
            }
        }
        info.setText(sbuf);
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null && checkBox2.isChecked()) {
            info.setCheckable(true);
            info.setChecked(true);
        }
    }

    public void setChecked(final boolean checked, boolean animated) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 == null) {
            return;
        }
        if (checkBox2.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
        AnimatorSet animatorSet = this.animator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animator = null;
        }
        float f = 1.0f;
        if (animated) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animator = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            Property<ContextLinkCell, Float> property = this.IMAGE_SCALE;
            float[] fArr = new float[1];
            if (checked) {
                f = 0.81f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.animator.setDuration(200L);
            this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.ContextLinkCell.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (ContextLinkCell.this.animator != null && ContextLinkCell.this.animator.equals(animation)) {
                        ContextLinkCell.this.animator = null;
                        if (!checked) {
                            ContextLinkCell.this.setBackgroundColor(0);
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (ContextLinkCell.this.animator != null && ContextLinkCell.this.animator.equals(animation)) {
                        ContextLinkCell.this.animator = null;
                    }
                }
            });
            this.animator.start();
            return;
        }
        if (checked) {
            f = 0.85f;
        }
        this.imageScale = f;
        invalidate();
    }
}
