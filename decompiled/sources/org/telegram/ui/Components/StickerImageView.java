package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class StickerImageView extends BackupImageView implements NotificationCenter.NotificationCenterDelegate {
    int currentAccount;
    int stickerNum;
    String stickerPackName = AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME;

    public StickerImageView(Context context, int currentAccount) {
        super(context);
        this.currentAccount = currentAccount;
    }

    public void setStickerNum(int stickerNum) {
        if (this.stickerNum != stickerNum) {
            this.stickerNum = stickerNum;
            setSticker();
        }
    }

    public void setStickerPackName(String stickerPackName) {
        this.stickerPackName = stickerPackName;
    }

    @Override // org.telegram.ui.Components.BackupImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSticker();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    @Override // org.telegram.ui.Components.BackupImageView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.diceStickersDidLoad) {
            String name = (String) args[0];
            if (this.stickerPackName.equals(name)) {
                setSticker();
            }
        }
    }

    public void setSticker() {
        SvgHelper.SvgDrawable svgThumb;
        TLRPC.Document document = null;
        TLRPC.TL_messages_stickerSet set = MediaDataController.getInstance(this.currentAccount).getStickerSetByName(this.stickerPackName);
        if (set == null) {
            set = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName(this.stickerPackName);
        }
        if (set != null && set.documents.size() > this.stickerNum) {
            document = set.documents.get(this.stickerNum);
        }
        if (document == null) {
            svgThumb = null;
        } else {
            svgThumb = DocumentObject.getSvgThumb(document.thumbs, Theme.key_emptyListPlaceholder, 0.2f);
        }
        if (svgThumb != null) {
            svgThumb.overrideWidthAndHeight(512, 512);
        }
        if (document != null) {
            ImageLocation imageLocation = ImageLocation.getForDocument(document);
            setImage(imageLocation, "130_130", "tgs", svgThumb, set);
            return;
        }
        this.imageReceiver.clearImage();
        MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName(this.stickerPackName, false, set == null);
    }
}
