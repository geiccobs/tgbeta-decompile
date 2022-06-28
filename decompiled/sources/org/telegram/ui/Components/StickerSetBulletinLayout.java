package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes5.dex */
public class StickerSetBulletinLayout extends Bulletin.TwoLineLayout {
    public static final int TYPE_ADDED = 2;
    public static final int TYPE_ADDED_TO_FAVORITES = 5;
    public static final int TYPE_ARCHIVED = 1;
    public static final int TYPE_EMPTY = -1;
    public static final int TYPE_REMOVED = 0;
    public static final int TYPE_REMOVED_FROM_FAVORITES = 4;
    public static final int TYPE_REMOVED_FROM_RECENT = 3;
    public static final int TYPE_REPLACED_TO_FAVORITES = 6;
    public static final int TYPE_REPLACED_TO_FAVORITES_GIFS = 7;

    /* loaded from: classes5.dex */
    public @interface Type {
    }

    public StickerSetBulletinLayout(Context context, TLObject setObject, int type) {
        this(context, setObject, type, null, null);
    }

    public StickerSetBulletinLayout(final Context context, TLObject setObject, int type, TLRPC.Document sticker, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        TLRPC.StickerSet stickerSet;
        TLRPC.Document sticker2;
        TLObject object;
        ImageLocation imageLocation;
        TLRPC.Document sticker3;
        TLRPC.Document sticker4;
        if (setObject instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_stickerSet obj = (TLRPC.TL_messages_stickerSet) setObject;
            TLRPC.StickerSet stickerSet2 = obj.set;
            ArrayList<TLRPC.Document> documents = obj.documents;
            if (documents != null && !documents.isEmpty()) {
                sticker4 = documents.get(0);
            } else {
                sticker4 = null;
            }
            stickerSet = stickerSet2;
            sticker2 = sticker4;
        } else if (setObject instanceof TLRPC.StickerSetCovered) {
            TLRPC.StickerSetCovered obj2 = (TLRPC.StickerSetCovered) setObject;
            TLRPC.StickerSet stickerSet3 = obj2.set;
            if (obj2.cover != null) {
                sticker3 = obj2.cover;
            } else if (!obj2.covers.isEmpty()) {
                sticker3 = obj2.covers.get(0);
            } else {
                sticker3 = null;
            }
            stickerSet = stickerSet3;
            sticker2 = sticker3;
        } else if (sticker == null && setObject != null && BuildVars.DEBUG_VERSION) {
            throw new IllegalArgumentException("Invalid type of the given setObject: " + setObject.getClass());
        } else {
            sticker2 = sticker;
            stickerSet = null;
        }
        if (sticker2 != null) {
            TLObject object2 = stickerSet == null ? null : FileLoader.getClosestPhotoSizeWithSize(stickerSet.thumbs, 90);
            if (object2 != null) {
                object = object2;
            } else {
                object = sticker2;
            }
            if (object instanceof TLRPC.Document) {
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(sticker2.thumbs, 90);
                imageLocation = ImageLocation.getForDocument(thumb, sticker2);
            } else {
                TLRPC.PhotoSize thumb2 = (TLRPC.PhotoSize) object;
                int thumbVersion = 0;
                if (setObject instanceof TLRPC.StickerSetCovered) {
                    thumbVersion = ((TLRPC.StickerSetCovered) setObject).set.thumb_version;
                } else if (setObject instanceof TLRPC.TL_messages_stickerSet) {
                    thumbVersion = ((TLRPC.TL_messages_stickerSet) setObject).set.thumb_version;
                }
                imageLocation = ImageLocation.getForSticker(thumb2, sticker2, thumbVersion);
            }
            if (((object instanceof TLRPC.Document) && MessageObject.isAnimatedStickerDocument(sticker2, true)) || MessageObject.isVideoSticker(sticker2) || MessageObject.isGifDocument(sticker2)) {
                this.imageView.setImage(ImageLocation.getForDocument(sticker2), "50_50", imageLocation, (String) null, 0, setObject);
            } else if (imageLocation != null && imageLocation.imageType == 1) {
                this.imageView.setImage(imageLocation, "50_50", "tgs", (Drawable) null, setObject);
            } else {
                this.imageView.setImage(imageLocation, "50_50", "webp", (Drawable) null, setObject);
            }
        } else {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, setObject);
        }
        switch (type) {
            case 0:
                if (stickerSet.masks) {
                    this.titleTextView.setText(LocaleController.getString("MasksRemoved", R.string.MasksRemoved));
                    this.subtitleTextView.setText(LocaleController.formatString("MasksRemovedInfo", R.string.MasksRemovedInfo, stickerSet.title));
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("StickersRemoved", R.string.StickersRemoved));
                this.subtitleTextView.setText(LocaleController.formatString("StickersRemovedInfo", R.string.StickersRemovedInfo, stickerSet.title));
                return;
            case 1:
                if (stickerSet.masks) {
                    this.titleTextView.setText(LocaleController.getString("MasksArchived", R.string.MasksArchived));
                    this.subtitleTextView.setText(LocaleController.formatString("MasksArchivedInfo", R.string.MasksArchivedInfo, stickerSet.title));
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("StickersArchived", R.string.StickersArchived));
                this.subtitleTextView.setText(LocaleController.formatString("StickersArchivedInfo", R.string.StickersArchivedInfo, stickerSet.title));
                return;
            case 2:
                if (stickerSet.masks) {
                    this.titleTextView.setText(LocaleController.getString("AddMasksInstalled", R.string.AddMasksInstalled));
                    this.subtitleTextView.setText(LocaleController.formatString("AddMasksInstalledInfo", R.string.AddMasksInstalledInfo, stickerSet.title));
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("AddStickersInstalled", R.string.AddStickersInstalled));
                this.subtitleTextView.setText(LocaleController.formatString("AddStickersInstalledInfo", R.string.AddStickersInstalledInfo, stickerSet.title));
                return;
            case 3:
                this.titleTextView.setText(LocaleController.getString("RemovedFromRecent", R.string.RemovedFromRecent));
                this.subtitleTextView.setVisibility(8);
                return;
            case 4:
                this.titleTextView.setText(LocaleController.getString("RemovedFromFavorites", R.string.RemovedFromFavorites));
                this.subtitleTextView.setVisibility(8);
                return;
            case 5:
                this.titleTextView.setText(LocaleController.getString("AddedToFavorites", R.string.AddedToFavorites));
                this.subtitleTextView.setVisibility(8);
                return;
            case 6:
                if (!UserConfig.getInstance(UserConfig.selectedAccount).isPremium() && !MessagesController.getInstance(UserConfig.selectedAccount).premiumLocked) {
                    this.titleTextView.setText(LocaleController.formatString("LimitReachedFavoriteStickers", R.string.LimitReachedFavoriteStickers, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).stickersFavedLimitDefault)));
                    CharSequence str = AndroidUtilities.replaceSingleTag(LocaleController.formatString("LimitReachedFavoriteStickersSubtitle", R.string.LimitReachedFavoriteStickersSubtitle, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).stickersFavedLimitPremium)), new Runnable() { // from class: org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            StickerSetBulletinLayout.lambda$new$0(context);
                        }
                    });
                    this.subtitleTextView.setText(str);
                    return;
                }
                this.titleTextView.setText(LocaleController.formatString("LimitReachedFavoriteStickers", R.string.LimitReachedFavoriteStickers, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).stickersFavedLimitPremium)));
                this.subtitleTextView.setText(LocaleController.formatString("LimitReachedFavoriteStickersSubtitlePremium", R.string.LimitReachedFavoriteStickersSubtitlePremium, new Object[0]));
                return;
            case 7:
                if (!UserConfig.getInstance(UserConfig.selectedAccount).isPremium() && !MessagesController.getInstance(UserConfig.selectedAccount).premiumLocked) {
                    this.titleTextView.setText(LocaleController.formatString("LimitReachedFavoriteGifs", R.string.LimitReachedFavoriteGifs, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).savedGifsLimitDefault)));
                    CharSequence str2 = AndroidUtilities.replaceSingleTag(LocaleController.formatString("LimitReachedFavoriteGifsSubtitle", R.string.LimitReachedFavoriteGifsSubtitle, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).savedGifsLimitPremium)), new Runnable() { // from class: org.telegram.ui.Components.StickerSetBulletinLayout$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            StickerSetBulletinLayout.lambda$new$1(context);
                        }
                    });
                    this.subtitleTextView.setText(str2);
                    return;
                }
                this.titleTextView.setText(LocaleController.formatString("LimitReachedFavoriteGifs", R.string.LimitReachedFavoriteGifs, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).savedGifsLimitPremium)));
                this.subtitleTextView.setText(LocaleController.formatString("LimitReachedFavoriteGifsSubtitlePremium", R.string.LimitReachedFavoriteGifsSubtitlePremium, new Object[0]));
                return;
            default:
                return;
        }
    }

    public static /* synthetic */ void lambda$new$0(Context context) {
        Activity activity = AndroidUtilities.findActivity(context);
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).m3653lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new PremiumPreviewFragment(LimitReachedBottomSheet.limitTypeToServerString(10)));
        }
    }

    public static /* synthetic */ void lambda$new$1(Context context) {
        Activity activity = AndroidUtilities.findActivity(context);
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).m3653lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new PremiumPreviewFragment(LimitReachedBottomSheet.limitTypeToServerString(9)));
        }
    }
}
