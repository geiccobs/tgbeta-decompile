package org.telegram.messenger;

import com.google.android.exoplayer2.text.ttml.TtmlNode;
import org.telegram.messenger.DocumentObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes4.dex */
public class ImageLocation {
    public static final int TYPE_BIG = 0;
    public static final int TYPE_SMALL = 1;
    public static final int TYPE_STRIPPED = 2;
    public static final int TYPE_VIDEO_THUMB = 3;
    public long access_hash;
    public long currentSize;
    public int dc_id;
    public TLRPC.Document document;
    public long documentId;
    public byte[] file_reference;
    public int imageType;
    public byte[] iv;
    public byte[] key;
    public TLRPC.TL_fileLocationToBeDeprecated location;
    public String path;
    public TLRPC.Photo photo;
    public long photoId;
    public TLRPC.InputPeer photoPeer;
    public int photoPeerType;
    public TLRPC.PhotoSize photoSize;
    public SecureDocument secureDocument;
    public TLRPC.InputStickerSet stickerSet;
    public String thumbSize;
    public int thumbVersion;
    public long videoSeekTo;
    public WebFile webFile;

    public static ImageLocation getForPath(String path) {
        if (path == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.path = path;
        return imageLocation;
    }

    public static ImageLocation getForSecureDocument(SecureDocument secureDocument) {
        if (secureDocument == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.secureDocument = secureDocument;
        return imageLocation;
    }

    public static ImageLocation getForDocument(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.document = document;
        imageLocation.key = document.key;
        imageLocation.iv = document.iv;
        imageLocation.currentSize = document.size;
        return imageLocation;
    }

    public static ImageLocation getForWebFile(WebFile webFile) {
        if (webFile == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.webFile = webFile;
        imageLocation.currentSize = webFile.size;
        return imageLocation;
    }

    public static ImageLocation getForObject(TLRPC.PhotoSize photoSize, TLObject object) {
        if (object instanceof TLRPC.Photo) {
            return getForPhoto(photoSize, (TLRPC.Photo) object);
        }
        if (object instanceof TLRPC.Document) {
            return getForDocument(photoSize, (TLRPC.Document) object);
        }
        return null;
    }

    public static ImageLocation getForPhoto(TLRPC.PhotoSize photoSize, TLRPC.Photo photo) {
        int dc_id;
        if ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize;
            return imageLocation;
        } else if (photoSize == null || photo == null) {
            return null;
        } else {
            if (photo.dc_id != 0) {
                dc_id = photo.dc_id;
            } else {
                dc_id = photoSize.location.dc_id;
            }
            return getForPhoto(photoSize.location, photoSize.size, photo, null, null, 1, dc_id, null, photoSize.type);
        }
    }

    public static ImageLocation getForUserOrChat(TLObject object, int type) {
        if (object instanceof TLRPC.User) {
            return getForUser((TLRPC.User) object, type);
        }
        if (object instanceof TLRPC.Chat) {
            return getForChat((TLRPC.Chat) object, type);
        }
        return null;
    }

    public static ImageLocation getForUser(TLRPC.User user, int type) {
        int dc_id;
        TLRPC.UserFull userFull;
        if (user == null || user.access_hash == 0 || user.photo == null) {
            return null;
        }
        if (type == 3) {
            int currentAccount = UserConfig.selectedAccount;
            if (!MessagesController.getInstance(currentAccount).isPremiumUser(user) || !user.photo.has_video || (userFull = MessagesController.getInstance(currentAccount).getUserFull(user.id)) == null || userFull.profile_photo == null || userFull.profile_photo.video_sizes == null || userFull.profile_photo.video_sizes.isEmpty()) {
                return null;
            }
            TLRPC.VideoSize videoSize = userFull.profile_photo.video_sizes.get(0);
            int i = 0;
            while (true) {
                if (i >= userFull.profile_photo.video_sizes.size()) {
                    break;
                } else if (!TtmlNode.TAG_P.equals(userFull.profile_photo.video_sizes.get(i).type)) {
                    i++;
                } else {
                    TLRPC.VideoSize videoSize2 = userFull.profile_photo.video_sizes.get(i);
                    videoSize = videoSize2;
                    break;
                }
            }
            return getForPhoto(videoSize, userFull.profile_photo);
        } else if (type == 2) {
            if (user.photo.stripped_thumb == null) {
                return null;
            }
            ImageLocation imageLocation = new ImageLocation();
            TLRPC.TL_photoStrippedSize tL_photoStrippedSize = new TLRPC.TL_photoStrippedSize();
            imageLocation.photoSize = tL_photoStrippedSize;
            tL_photoStrippedSize.type = "s";
            imageLocation.photoSize.bytes = user.photo.stripped_thumb;
            return imageLocation;
        } else {
            TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
            TLRPC.FileLocation fileLocation = type == 0 ? userProfilePhoto.photo_big : userProfilePhoto.photo_small;
            if (fileLocation == null) {
                return null;
            }
            TLRPC.TL_inputPeerUser inputPeer = new TLRPC.TL_inputPeerUser();
            inputPeer.user_id = user.id;
            inputPeer.access_hash = user.access_hash;
            if (user.photo.dc_id != 0) {
                dc_id = user.photo.dc_id;
            } else {
                int dc_id2 = fileLocation.dc_id;
                dc_id = dc_id2;
            }
            ImageLocation location = getForPhoto(fileLocation, 0, null, null, inputPeer, type, dc_id, null, null);
            location.photoId = user.photo.photo_id;
            return location;
        }
    }

    public static ImageLocation getForChat(TLRPC.Chat chat, int type) {
        TLRPC.InputPeer inputPeer;
        int dc_id;
        if (chat == null || chat.photo == null) {
            return null;
        }
        if (type == 2) {
            if (chat.photo.stripped_thumb == null) {
                return null;
            }
            ImageLocation imageLocation = new ImageLocation();
            TLRPC.TL_photoStrippedSize tL_photoStrippedSize = new TLRPC.TL_photoStrippedSize();
            imageLocation.photoSize = tL_photoStrippedSize;
            tL_photoStrippedSize.type = "s";
            imageLocation.photoSize.bytes = chat.photo.stripped_thumb;
            return imageLocation;
        }
        TLRPC.ChatPhoto chatPhoto = chat.photo;
        TLRPC.FileLocation fileLocation = type == 0 ? chatPhoto.photo_big : chatPhoto.photo_small;
        if (fileLocation == null) {
            return null;
        }
        if (ChatObject.isChannel(chat)) {
            if (chat.access_hash == 0) {
                return null;
            }
            inputPeer = new TLRPC.TL_inputPeerChannel();
            inputPeer.channel_id = chat.id;
            inputPeer.access_hash = chat.access_hash;
        } else {
            inputPeer = new TLRPC.TL_inputPeerChat();
            inputPeer.chat_id = chat.id;
        }
        if (chat.photo.dc_id != 0) {
            dc_id = chat.photo.dc_id;
        } else {
            int dc_id2 = fileLocation.dc_id;
            dc_id = dc_id2;
        }
        ImageLocation location = getForPhoto(fileLocation, 0, null, null, inputPeer, type, dc_id, null, null);
        location.photoId = chat.photo.photo_id;
        return location;
    }

    public static ImageLocation getForSticker(TLRPC.PhotoSize photoSize, TLRPC.Document sticker, int thumbVersion) {
        TLRPC.InputStickerSet stickerSet;
        if ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize;
            return imageLocation;
        } else if (photoSize == null || sticker == null || (stickerSet = MediaDataController.getInputStickerSet(sticker)) == null) {
            return null;
        } else {
            ImageLocation imageLocation2 = getForPhoto(photoSize.location, photoSize.size, null, null, null, 1, sticker.dc_id, stickerSet, photoSize.type);
            if (MessageObject.isAnimatedStickerDocument(sticker, true)) {
                imageLocation2.imageType = 1;
            }
            imageLocation2.thumbVersion = thumbVersion;
            return imageLocation2;
        }
    }

    public static ImageLocation getForDocument(TLRPC.VideoSize videoSize, TLRPC.Document document) {
        if (videoSize == null || document == null) {
            return null;
        }
        ImageLocation location = getForPhoto(videoSize.location, videoSize.size, null, document, null, 1, document.dc_id, null, videoSize.type);
        if ("f".equals(videoSize.type)) {
            location.imageType = 1;
        } else {
            location.imageType = 2;
        }
        return location;
    }

    public static ImageLocation getForPhoto(TLRPC.VideoSize videoSize, TLRPC.Photo photo) {
        if (videoSize == null || photo == null) {
            return null;
        }
        ImageLocation location = getForPhoto(videoSize.location, videoSize.size, photo, null, null, 1, photo.dc_id, null, videoSize.type);
        location.imageType = 2;
        if ((videoSize.flags & 1) != 0) {
            location.videoSeekTo = (int) (videoSize.video_start_ts * 1000.0d);
        }
        return location;
    }

    public static ImageLocation getForDocument(TLRPC.PhotoSize photoSize, TLRPC.Document document) {
        if ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize;
            return imageLocation;
        } else if (photoSize == null || document == null) {
            return null;
        } else {
            return getForPhoto(photoSize.location, photoSize.size, null, document, null, 1, document.dc_id, null, photoSize.type);
        }
    }

    public static ImageLocation getForLocal(TLRPC.FileLocation location) {
        if (location == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = new TLRPC.TL_fileLocationToBeDeprecated();
        imageLocation.location = tL_fileLocationToBeDeprecated;
        tL_fileLocationToBeDeprecated.local_id = location.local_id;
        imageLocation.location.volume_id = location.volume_id;
        imageLocation.location.secret = location.secret;
        imageLocation.location.dc_id = location.dc_id;
        return imageLocation;
    }

    private static ImageLocation getForPhoto(TLRPC.FileLocation location, int size, TLRPC.Photo photo, TLRPC.Document document, TLRPC.InputPeer photoPeer, int photoPeerType, int dc_id, TLRPC.InputStickerSet stickerSet, String thumbSize) {
        if (location != null) {
            if (photo == null && photoPeer == null && stickerSet == null && document == null) {
                return null;
            }
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.dc_id = dc_id;
            imageLocation.photo = photo;
            imageLocation.currentSize = size;
            imageLocation.photoPeer = photoPeer;
            imageLocation.photoPeerType = photoPeerType;
            imageLocation.stickerSet = stickerSet;
            if (location instanceof TLRPC.TL_fileLocationToBeDeprecated) {
                imageLocation.location = (TLRPC.TL_fileLocationToBeDeprecated) location;
                if (photo != null) {
                    imageLocation.file_reference = photo.file_reference;
                    imageLocation.access_hash = photo.access_hash;
                    imageLocation.photoId = photo.id;
                    imageLocation.thumbSize = thumbSize;
                } else if (document != null) {
                    imageLocation.file_reference = document.file_reference;
                    imageLocation.access_hash = document.access_hash;
                    imageLocation.documentId = document.id;
                    imageLocation.thumbSize = thumbSize;
                }
            } else {
                TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = new TLRPC.TL_fileLocationToBeDeprecated();
                imageLocation.location = tL_fileLocationToBeDeprecated;
                tL_fileLocationToBeDeprecated.local_id = location.local_id;
                imageLocation.location.volume_id = location.volume_id;
                imageLocation.location.secret = location.secret;
                imageLocation.dc_id = location.dc_id;
                imageLocation.file_reference = location.file_reference;
                imageLocation.key = location.key;
                imageLocation.iv = location.iv;
                imageLocation.access_hash = location.secret;
            }
            return imageLocation;
        }
        return null;
    }

    public static String getStrippedKey(Object parentObject, Object fullObject, Object strippedObject) {
        if (parentObject instanceof TLRPC.WebPage) {
            if (fullObject instanceof ImageLocation) {
                ImageLocation imageLocation = (ImageLocation) fullObject;
                if (imageLocation.document != null) {
                    fullObject = imageLocation.document;
                } else if (imageLocation.photoSize != null) {
                    fullObject = imageLocation.photoSize;
                } else if (imageLocation.photo != null) {
                    fullObject = imageLocation.photo;
                }
            }
            if (fullObject == null) {
                return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + strippedObject;
            } else if (fullObject instanceof TLRPC.Document) {
                TLRPC.Document document = (TLRPC.Document) fullObject;
                return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + document.id;
            } else if (fullObject instanceof TLRPC.Photo) {
                TLRPC.Photo photo = (TLRPC.Photo) fullObject;
                return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + photo.id;
            } else if (fullObject instanceof TLRPC.PhotoSize) {
                TLRPC.PhotoSize size = (TLRPC.PhotoSize) fullObject;
                if (size.location != null) {
                    return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + size.location.local_id + "_" + size.location.volume_id;
                }
                return "stripped" + FileRefController.getKeyForParentObject(parentObject);
            } else if (fullObject instanceof TLRPC.FileLocation) {
                TLRPC.FileLocation loc = (TLRPC.FileLocation) fullObject;
                return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + loc.local_id + "_" + loc.volume_id;
            }
        }
        return "stripped" + FileRefController.getKeyForParentObject(parentObject);
    }

    public String getKey(Object parentObject, Object fullObject, boolean url) {
        if (this.secureDocument != null) {
            return this.secureDocument.secureFile.dc_id + "_" + this.secureDocument.secureFile.id;
        }
        TLRPC.PhotoSize photoSize = this.photoSize;
        if ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
            if (photoSize.bytes.length > 0) {
                return getStrippedKey(parentObject, fullObject, this.photoSize);
            }
            return null;
        } else if (this.location != null) {
            return this.location.volume_id + "_" + this.location.local_id;
        } else {
            WebFile webFile = this.webFile;
            if (webFile != null) {
                return Utilities.MD5(webFile.url);
            }
            TLRPC.Document document = this.document;
            if (document != null) {
                if (!url && (document instanceof DocumentObject.ThemeDocument)) {
                    DocumentObject.ThemeDocument themeDocument = (DocumentObject.ThemeDocument) document;
                    StringBuilder sb = new StringBuilder();
                    sb.append(this.document.dc_id);
                    sb.append("_");
                    sb.append(this.document.id);
                    sb.append("_");
                    sb.append(Theme.getBaseThemeKey(themeDocument.themeSettings));
                    sb.append("_");
                    sb.append(themeDocument.themeSettings.accent_color);
                    sb.append("_");
                    int i = 0;
                    sb.append(themeDocument.themeSettings.message_colors.size() > 1 ? themeDocument.themeSettings.message_colors.get(1).intValue() : 0);
                    sb.append("_");
                    if (themeDocument.themeSettings.message_colors.size() > 0) {
                        i = themeDocument.themeSettings.message_colors.get(0).intValue();
                    }
                    sb.append(i);
                    return sb.toString();
                } else if (document.id != 0 && this.document.dc_id != 0) {
                    return this.document.dc_id + "_" + this.document.id;
                } else {
                    return null;
                }
            }
            String str = this.path;
            if (str != null) {
                return Utilities.MD5(str);
            }
            return null;
        }
    }

    public boolean isEncrypted() {
        return this.key != null;
    }

    public long getSize() {
        TLRPC.PhotoSize photoSize = this.photoSize;
        if (photoSize != null) {
            return photoSize.size;
        }
        SecureDocument secureDocument = this.secureDocument;
        if (secureDocument != null) {
            if (secureDocument.secureFile != null) {
                return this.secureDocument.secureFile.size;
            }
        } else {
            TLRPC.Document document = this.document;
            if (document != null) {
                return document.size;
            }
            WebFile webFile = this.webFile;
            if (webFile != null) {
                return webFile.size;
            }
        }
        return this.currentSize;
    }
}
