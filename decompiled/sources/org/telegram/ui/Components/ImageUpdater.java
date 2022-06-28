package org.telegram.ui.Components;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BasePermissionsActivity;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoAlbumPickerActivity;
import org.telegram.ui.PhotoCropActivity;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes5.dex */
public class ImageUpdater implements NotificationCenter.NotificationCenterDelegate, PhotoCropActivity.PhotoEditActivityDelegate {
    private static final int ID_RECORD_VIDEO = 4;
    private static final int ID_REMOVE_PHOTO = 3;
    private static final int ID_SEARCH_WEB = 2;
    private static final int ID_TAKE_PHOTO = 0;
    private static final int ID_UPLOAD_FROM_GALLERY = 1;
    private static final int attach_photo = 0;
    private TLRPC.PhotoSize bigPhoto;
    private boolean canSelectVideo;
    private ChatAttachAlert chatAttachAlert;
    private boolean clearAfterUpdate;
    private MessageObject convertingVideo;
    public String currentPicturePath;
    private ImageUpdaterDelegate delegate;
    private String finalPath;
    private boolean forceDarkTheme;
    private boolean openWithFrontfaceCamera;
    public BaseFragment parentFragment;
    private boolean showingFromDialog;
    private TLRPC.PhotoSize smallPhoto;
    private TLRPC.InputFile uploadedPhoto;
    private TLRPC.InputFile uploadedVideo;
    private String uploadingImage;
    private String uploadingVideo;
    private String videoPath;
    private double videoTimestamp;
    private int currentAccount = UserConfig.selectedAccount;
    private File picturePath = null;
    private boolean useAttachMenu = true;
    private boolean searchAvailable = true;
    private boolean uploadAfterSelect = true;
    private ImageReceiver imageReceiver = new ImageReceiver(null);

    /* loaded from: classes5.dex */
    public interface ImageUpdaterDelegate {
        void didStartUpload(boolean z);

        void didUploadPhoto(TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, double d, String str, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2);

        String getInitialSearchString();

        void onUploadProgressChanged(float f);

        /* renamed from: org.telegram.ui.Components.ImageUpdater$ImageUpdaterDelegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static String $default$getInitialSearchString(ImageUpdaterDelegate _this) {
                return null;
            }

            public static void $default$onUploadProgressChanged(ImageUpdaterDelegate _this, float progress) {
            }

            public static void $default$didStartUpload(ImageUpdaterDelegate _this, boolean isVideo) {
            }
        }
    }

    public boolean isUploadingImage() {
        return (this.uploadingImage == null && this.uploadingVideo == null && this.convertingVideo == null) ? false : true;
    }

    public void clear() {
        if (this.uploadingImage != null || this.uploadingVideo != null || this.convertingVideo != null) {
            this.clearAfterUpdate = true;
        } else {
            this.parentFragment = null;
            this.delegate = null;
        }
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.dismissInternal();
            this.chatAttachAlert.onDestroy();
        }
    }

    public void setOpenWithFrontfaceCamera(boolean value) {
        this.openWithFrontfaceCamera = value;
    }

    public ImageUpdater(boolean allowVideo) {
        boolean z = true;
        this.canSelectVideo = (!allowVideo || Build.VERSION.SDK_INT <= 18) ? false : z;
    }

    public void setDelegate(ImageUpdaterDelegate imageUpdaterDelegate) {
        this.delegate = imageUpdaterDelegate;
    }

    public void openMenu(boolean hasAvatar, final Runnable onDeleteAvatar, DialogInterface.OnDismissListener onDismiss) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        if (this.useAttachMenu) {
            openAttachMenu(onDismiss);
            return;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(this.parentFragment.getParentActivity());
        builder.setTitle(LocaleController.getString("ChoosePhoto", R.string.ChoosePhoto), true);
        ArrayList<CharSequence> items = new ArrayList<>();
        ArrayList<Integer> icons = new ArrayList<>();
        final ArrayList<Integer> ids = new ArrayList<>();
        items.add(LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto));
        icons.add(Integer.valueOf((int) R.drawable.msg_camera));
        ids.add(0);
        if (this.canSelectVideo) {
            items.add(LocaleController.getString("ChooseRecordVideo", R.string.ChooseRecordVideo));
            icons.add(Integer.valueOf((int) R.drawable.msg_video));
            ids.add(4);
        }
        items.add(LocaleController.getString("ChooseFromGallery", R.string.ChooseFromGallery));
        icons.add(Integer.valueOf((int) R.drawable.msg_photos));
        ids.add(1);
        if (this.searchAvailable) {
            items.add(LocaleController.getString("ChooseFromSearch", R.string.ChooseFromSearch));
            icons.add(Integer.valueOf((int) R.drawable.msg_search));
            ids.add(2);
        }
        if (hasAvatar) {
            items.add(LocaleController.getString("DeletePhoto", R.string.DeletePhoto));
            icons.add(Integer.valueOf((int) R.drawable.msg_delete));
            ids.add(3);
        }
        int[] iconsRes = new int[icons.size()];
        int N = icons.size();
        for (int i = 0; i < N; i++) {
            iconsRes[i] = icons.get(i).intValue();
        }
        builder.setItems((CharSequence[]) items.toArray(new CharSequence[0]), iconsRes, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ImageUpdater$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                ImageUpdater.this.m2677lambda$openMenu$0$orgtelegramuiComponentsImageUpdater(ids, onDeleteAvatar, dialogInterface, i2);
            }
        });
        BottomSheet sheet = builder.create();
        sheet.setOnHideListener(onDismiss);
        this.parentFragment.showDialog(sheet);
        if (hasAvatar) {
            sheet.setItemColor(items.size() - 1, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
        }
    }

    /* renamed from: lambda$openMenu$0$org-telegram-ui-Components-ImageUpdater */
    public /* synthetic */ void m2677lambda$openMenu$0$orgtelegramuiComponentsImageUpdater(ArrayList ids, Runnable onDeleteAvatar, DialogInterface dialogInterface, int i) {
        int id = ((Integer) ids.get(i)).intValue();
        switch (id) {
            case 0:
                openCamera();
                return;
            case 1:
                openGallery();
                return;
            case 2:
                openSearch();
                return;
            case 3:
                onDeleteAvatar.run();
                return;
            case 4:
                openVideoCamera();
                return;
            default:
                return;
        }
    }

    public void setSearchAvailable(boolean value) {
        this.searchAvailable = value;
        this.useAttachMenu = value;
    }

    public void setSearchAvailable(boolean value, boolean useAttachMenu) {
        this.useAttachMenu = useAttachMenu;
        this.searchAvailable = value;
    }

    public void setUploadAfterSelect(boolean value) {
        this.uploadAfterSelect = value;
    }

    public void onResume() {
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.onResume();
        }
    }

    public void onPause() {
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.onPause();
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert;
    }

    public boolean dismissCurrentDialog(Dialog dialog) {
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert == null || dialog != chatAttachAlert) {
            return false;
        }
        chatAttachAlert.getPhotoLayout().closeCamera(false);
        this.chatAttachAlert.dismissInternal();
        this.chatAttachAlert.getPhotoLayout().hideCamera(true);
        return true;
    }

    public void openSearch() {
        if (this.parentFragment == null) {
            return;
        }
        final HashMap<Object, Object> photos = new HashMap<>();
        final ArrayList<Object> order = new ArrayList<>();
        PhotoPickerActivity fragment = new PhotoPickerActivity(0, null, photos, order, 1, false, null, this.forceDarkTheme);
        fragment.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() { // from class: org.telegram.ui.Components.ImageUpdater.1
            private boolean sendPressed;

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public /* synthetic */ void onOpenInPressed() {
                PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
            }

            {
                ImageUpdater.this = this;
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public void selectedPhotosChanged() {
            }

            private void sendSelectedPhotos(HashMap<Object, Object> photos2, ArrayList<Object> order2, boolean notify, int scheduleDate) {
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate) {
                if (photos.isEmpty() || ImageUpdater.this.delegate == null || this.sendPressed || canceled) {
                    return;
                }
                this.sendPressed = true;
                ArrayList<SendMessagesHelper.SendingMediaInfo> media = new ArrayList<>();
                for (int a = 0; a < order.size(); a++) {
                    Object object = photos.get(order.get(a));
                    SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                    media.add(info);
                    if (object instanceof MediaController.SearchImage) {
                        MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                        if (searchImage.imagePath != null) {
                            info.path = searchImage.imagePath;
                        } else {
                            info.searchImage = searchImage;
                        }
                        info.videoEditedInfo = searchImage.editedInfo;
                        info.thumbPath = searchImage.thumbPath;
                        info.caption = searchImage.caption != null ? searchImage.caption.toString() : null;
                        info.entities = searchImage.entities;
                        info.masks = searchImage.stickers;
                        info.ttl = searchImage.ttl;
                    }
                }
                ImageUpdater.this.didSelectPhotos(media);
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public void onCaptionChanged(CharSequence caption) {
            }
        });
        fragment.setMaxSelectedPhotos(1, false);
        fragment.setInitialSearchString(this.delegate.getInitialSearchString());
        if (this.showingFromDialog) {
            this.parentFragment.showAsSheet(fragment);
        } else {
            this.parentFragment.presentFragment(fragment);
        }
    }

    private void openAttachMenu(DialogInterface.OnDismissListener onDismissListener) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        createChatAttachView();
        this.chatAttachAlert.setOpenWithFrontFaceCamera(this.openWithFrontfaceCamera);
        this.chatAttachAlert.setMaxSelectedPhotos(1, false);
        this.chatAttachAlert.getPhotoLayout().loadGalleryPhotos();
        if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
            AndroidUtilities.hideKeyboard(this.parentFragment.getFragmentView().findFocus());
        }
        this.chatAttachAlert.init();
        this.chatAttachAlert.setOnHideListener(onDismissListener);
        this.parentFragment.showDialog(this.chatAttachAlert);
    }

    private void createChatAttachView() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null && this.chatAttachAlert == null) {
            ChatAttachAlert chatAttachAlert = new ChatAttachAlert(this.parentFragment.getParentActivity(), this.parentFragment, this.forceDarkTheme, this.showingFromDialog);
            this.chatAttachAlert = chatAttachAlert;
            chatAttachAlert.setAvatarPicker(this.canSelectVideo ? 2 : 1, this.searchAvailable);
            this.chatAttachAlert.setDelegate(new ChatAttachAlert.ChatAttachViewDelegate() { // from class: org.telegram.ui.Components.ImageUpdater.2
                {
                    ImageUpdater.this = this;
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public void didPressedButton(int button, boolean arg, boolean notify, int scheduleDate, boolean forceDocument) {
                    if (ImageUpdater.this.parentFragment == null || ImageUpdater.this.parentFragment.getParentActivity() == null || ImageUpdater.this.chatAttachAlert == null) {
                        return;
                    }
                    if (button == 8 || button == 7) {
                        HashMap<Object, Object> photos = ImageUpdater.this.chatAttachAlert.getPhotoLayout().getSelectedPhotos();
                        ArrayList<Object> order = ImageUpdater.this.chatAttachAlert.getPhotoLayout().getSelectedPhotosOrder();
                        ArrayList<SendMessagesHelper.SendingMediaInfo> media = new ArrayList<>();
                        for (int a = 0; a < order.size(); a++) {
                            Object object = photos.get(order.get(a));
                            SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                            media.add(info);
                            String str = null;
                            if (object instanceof MediaController.PhotoEntry) {
                                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                                if (photoEntry.imagePath != null) {
                                    info.path = photoEntry.imagePath;
                                } else {
                                    info.path = photoEntry.path;
                                }
                                info.thumbPath = photoEntry.thumbPath;
                                info.videoEditedInfo = photoEntry.editedInfo;
                                info.isVideo = photoEntry.isVideo;
                                if (photoEntry.caption != null) {
                                    str = photoEntry.caption.toString();
                                }
                                info.caption = str;
                                info.entities = photoEntry.entities;
                                info.masks = photoEntry.stickers;
                                info.ttl = photoEntry.ttl;
                            } else if (object instanceof MediaController.SearchImage) {
                                MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                                if (searchImage.imagePath != null) {
                                    info.path = searchImage.imagePath;
                                } else {
                                    info.searchImage = searchImage;
                                }
                                info.thumbPath = searchImage.thumbPath;
                                info.videoEditedInfo = searchImage.editedInfo;
                                if (searchImage.caption != null) {
                                    str = searchImage.caption.toString();
                                }
                                info.caption = str;
                                info.entities = searchImage.entities;
                                info.masks = searchImage.stickers;
                                info.ttl = searchImage.ttl;
                                if (searchImage.inlineResult != null && searchImage.type == 1) {
                                    info.inlineResult = searchImage.inlineResult;
                                    info.params = searchImage.params;
                                }
                                searchImage.date = (int) (System.currentTimeMillis() / 1000);
                            }
                        }
                        ImageUpdater.this.didSelectPhotos(media);
                        if (button != 8) {
                            ImageUpdater.this.chatAttachAlert.dismiss(true);
                            return;
                        }
                        return;
                    }
                    ImageUpdater.this.chatAttachAlert.dismissWithButtonClick(button);
                    processSelectedAttach(button);
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public View getRevealView() {
                    return null;
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public void didSelectBot(TLRPC.User user) {
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public void onCameraOpened() {
                    AndroidUtilities.hideKeyboard(ImageUpdater.this.parentFragment.getFragmentView().findFocus());
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public boolean needEnterComment() {
                    return false;
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public void doOnIdle(Runnable runnable) {
                    runnable.run();
                }

                private void processSelectedAttach(int which) {
                    if (which == 0) {
                        ImageUpdater.this.openCamera();
                    }
                }

                @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
                public void openAvatarsSearch() {
                    ImageUpdater.this.openSearch();
                }
            });
        }
    }

    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos) {
        if (!photos.isEmpty()) {
            SendMessagesHelper.SendingMediaInfo info = photos.get(0);
            Bitmap bitmap = null;
            MessageObject avatarObject = null;
            if (info.isVideo || info.videoEditedInfo != null) {
                TLRPC.TL_message message = new TLRPC.TL_message();
                message.id = 0;
                message.message = "";
                message.media = new TLRPC.TL_messageMediaEmpty();
                message.action = new TLRPC.TL_messageActionEmpty();
                message.dialog_id = 0L;
                avatarObject = new MessageObject(UserConfig.selectedAccount, message, false, false);
                avatarObject.messageOwner.attachPath = new File(FileLoader.getDirectory(4), SharedConfig.getLastLocalId() + "_avatar.mp4").getAbsolutePath();
                avatarObject.videoEditedInfo = info.videoEditedInfo;
                bitmap = ImageLoader.loadBitmap(info.thumbPath, null, 800.0f, 800.0f, true);
            } else if (info.path != null) {
                bitmap = ImageLoader.loadBitmap(info.path, null, 800.0f, 800.0f, true);
            } else if (info.searchImage != null) {
                if (info.searchImage.photo != null) {
                    TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(info.searchImage.photo.sizes, AndroidUtilities.getPhotoSize());
                    if (photoSize != null) {
                        File path = FileLoader.getInstance(this.currentAccount).getPathToAttach(photoSize, true);
                        this.finalPath = path.getAbsolutePath();
                        if (!path.exists()) {
                            path = FileLoader.getInstance(this.currentAccount).getPathToAttach(photoSize, false);
                            if (!path.exists()) {
                                path = null;
                            }
                        }
                        if (path != null) {
                            bitmap = ImageLoader.loadBitmap(path.getAbsolutePath(), null, 800.0f, 800.0f, true);
                        } else {
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadFailed);
                            this.uploadingImage = FileLoader.getAttachFileName(photoSize.location);
                            this.imageReceiver.setImage(ImageLocation.getForPhoto(photoSize, info.searchImage.photo), null, null, "jpg", null, 1);
                        }
                    }
                } else if (info.searchImage.imageUrl != null) {
                    String md5 = Utilities.MD5(info.searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(info.searchImage.imageUrl, "jpg");
                    File cacheFile = new File(FileLoader.getDirectory(4), md5);
                    this.finalPath = cacheFile.getAbsolutePath();
                    if (cacheFile.exists() && cacheFile.length() != 0) {
                        bitmap = ImageLoader.loadBitmap(cacheFile.getAbsolutePath(), null, 800.0f, 800.0f, true);
                    } else {
                        this.uploadingImage = info.searchImage.imageUrl;
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidLoad);
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidFailedLoad);
                        this.imageReceiver.setImage(info.searchImage.imageUrl, null, null, "jpg", 1L);
                    }
                }
            }
            processBitmap(bitmap, avatarObject);
        }
    }

    public void openCamera() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= 23 && this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 20);
                return;
            }
            Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            File image = AndroidUtilities.generatePicturePath();
            if (image != null) {
                if (Build.VERSION.SDK_INT >= 24) {
                    takePictureIntent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "org.telegram.messenger.beta.provider", image));
                    takePictureIntent.addFlags(2);
                    takePictureIntent.addFlags(1);
                } else {
                    takePictureIntent.putExtra("output", Uri.fromFile(image));
                }
                this.currentPicturePath = image.getAbsolutePath();
            }
            this.parentFragment.startActivityForResult(takePictureIntent, 13);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void openVideoCamera() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= 23 && this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
                return;
            }
            Intent takeVideoIntent = new Intent("android.media.action.VIDEO_CAPTURE");
            File video = AndroidUtilities.generateVideoPath();
            if (video != null) {
                if (Build.VERSION.SDK_INT >= 24) {
                    takeVideoIntent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "org.telegram.messenger.beta.provider", video));
                    takeVideoIntent.addFlags(2);
                    takeVideoIntent.addFlags(1);
                } else if (Build.VERSION.SDK_INT >= 18) {
                    takeVideoIntent.putExtra("output", Uri.fromFile(video));
                }
                takeVideoIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                takeVideoIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                takeVideoIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                takeVideoIntent.putExtra("android.intent.extra.durationLimit", 10);
                this.currentPicturePath = video.getAbsolutePath();
            }
            this.parentFragment.startActivityForResult(takeVideoIntent, 15);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            if (requestCode == 17) {
                chatAttachAlert.getPhotoLayout().checkCamera(false);
                this.chatAttachAlert.getPhotoLayout().checkStorage();
            } else if (requestCode == 4) {
                chatAttachAlert.getPhotoLayout().checkStorage();
            }
        }
    }

    public void openGallery() {
        if (this.parentFragment == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 23 && this.parentFragment.getParentActivity() != null && this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
            this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE_FOR_AVATAR);
            return;
        }
        PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(this.canSelectVideo ? PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR_VIDEO : PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR, false, false, null);
        fragment.setAllowSearchImages(this.searchAvailable);
        fragment.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() { // from class: org.telegram.ui.Components.ImageUpdater.3
            {
                ImageUpdater.this = this;
            }

            @Override // org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate
            public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate) {
                ImageUpdater.this.didSelectPhotos(photos);
            }

            @Override // org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate
            public void startPhotoSelectActivity() {
                try {
                    Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
                    photoPickerIntent.setType("image/*");
                    ImageUpdater.this.parentFragment.startActivityForResult(photoPickerIntent, 14);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        });
        this.parentFragment.presentFragment(fragment);
    }

    private void startCrop(final String path, final Uri uri) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ImageUpdater$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ImageUpdater.this.m2678lambda$startCrop$1$orgtelegramuiComponentsImageUpdater(path, uri);
            }
        });
    }

    /* renamed from: lambda$startCrop$1$org-telegram-ui-Components-ImageUpdater */
    public /* synthetic */ void m2678lambda$startCrop$1$orgtelegramuiComponentsImageUpdater(String path, Uri uri) {
        try {
            LaunchActivity activity = (LaunchActivity) this.parentFragment.getParentActivity();
            if (activity == null) {
                return;
            }
            Bundle args = new Bundle();
            if (path != null) {
                args.putString("photoPath", path);
            } else if (uri != null) {
                args.putParcelable("photoUri", uri);
            }
            PhotoCropActivity photoCropActivity = new PhotoCropActivity(args);
            photoCropActivity.setDelegate(this);
            activity.m3653lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(photoCropActivity);
        } catch (Exception e) {
            FileLog.e(e);
            Bitmap bitmap = ImageLoader.loadBitmap(path, uri, 800.0f, 800.0f, true);
            processBitmap(bitmap, null);
        }
    }

    public void openPhotoForEdit(String path, String thumb, int orientation, boolean isVideo) {
        final ArrayList<Object> arrayList = new ArrayList<>();
        MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, 0, 0L, path, orientation, false, 0, 0, 0L);
        photoEntry.isVideo = isVideo;
        photoEntry.thumbPath = thumb;
        arrayList.add(photoEntry);
        PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
        PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 1, false, new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.Components.ImageUpdater.4
            {
                ImageUpdater.this = this;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean forceDocument) {
                Bitmap bitmap;
                String path2 = null;
                MediaController.PhotoEntry photoEntry2 = (MediaController.PhotoEntry) arrayList.get(0);
                if (photoEntry2.imagePath != null) {
                    path2 = photoEntry2.imagePath;
                } else if (photoEntry2.path != null) {
                    path2 = photoEntry2.path;
                }
                MessageObject avatarObject = null;
                if (photoEntry2.isVideo || photoEntry2.editedInfo != null) {
                    TLRPC.TL_message message = new TLRPC.TL_message();
                    message.id = 0;
                    message.message = "";
                    message.media = new TLRPC.TL_messageMediaEmpty();
                    message.action = new TLRPC.TL_messageActionEmpty();
                    message.dialog_id = 0L;
                    avatarObject = new MessageObject(UserConfig.selectedAccount, message, false, false);
                    TLRPC.Message message2 = avatarObject.messageOwner;
                    File directory = FileLoader.getDirectory(4);
                    message2.attachPath = new File(directory, SharedConfig.getLastLocalId() + "_avatar.mp4").getAbsolutePath();
                    avatarObject.videoEditedInfo = photoEntry2.editedInfo;
                    bitmap = ImageLoader.loadBitmap(photoEntry2.thumbPath, null, 800.0f, 800.0f, true);
                } else {
                    bitmap = ImageLoader.loadBitmap(path2, null, 800.0f, 800.0f, true);
                }
                ImageUpdater.this.processBitmap(bitmap, avatarObject);
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public boolean allowCaption() {
                return false;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public boolean canScrollAway() {
                return false;
            }
        }, null);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            if (requestCode == 0 || requestCode == 2) {
                createChatAttachView();
                ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
                if (chatAttachAlert != null) {
                    chatAttachAlert.onActivityResultFragment(requestCode, data, this.currentPicturePath);
                }
                this.currentPicturePath = null;
            } else if (requestCode != 13) {
                if (requestCode == 14) {
                    if (data != null && data.getData() != null) {
                        startCrop(null, data.getData());
                    }
                } else if (requestCode == 15) {
                    openPhotoForEdit(this.currentPicturePath, null, 0, true);
                    AndroidUtilities.addMediaToGallery(this.currentPicturePath);
                    this.currentPicturePath = null;
                }
            } else {
                this.parentFragment.getParentActivity().overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
                int orientation = 0;
                try {
                    ExifInterface ei = new ExifInterface(this.currentPicturePath);
                    int exif = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    switch (exif) {
                        case 3:
                            orientation = 180;
                            break;
                        case 6:
                            orientation = 90;
                            break;
                        case 8:
                            orientation = 270;
                            break;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                openPhotoForEdit(this.currentPicturePath, null, orientation, false);
                AndroidUtilities.addMediaToGallery(this.currentPicturePath);
                this.currentPicturePath = null;
            }
        }
    }

    public void processBitmap(Bitmap bitmap, MessageObject avatarObject) {
        if (bitmap == null) {
            return;
        }
        this.uploadedVideo = null;
        this.uploadedPhoto = null;
        this.convertingVideo = null;
        this.videoPath = null;
        this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, (int) GroupCallActivity.TABLET_LIST_SIZE, (int) GroupCallActivity.TABLET_LIST_SIZE);
        TLRPC.PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, 150.0f, 150.0f, 80, false, 150, 150);
        this.smallPhoto = scaleAndSaveImage;
        if (scaleAndSaveImage != null) {
            try {
                Bitmap b = BitmapFactory.decodeFile(FileLoader.getInstance(this.currentAccount).getPathToAttach(this.smallPhoto, true).getAbsolutePath());
                String key = this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50";
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(b), key, true);
            } catch (Throwable th) {
            }
        }
        bitmap.recycle();
        if (this.bigPhoto != null) {
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            this.uploadingImage = FileLoader.getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg";
            if (this.uploadAfterSelect) {
                if (avatarObject != null && avatarObject.videoEditedInfo != null) {
                    this.convertingVideo = avatarObject;
                    long j = 0;
                    if (avatarObject.videoEditedInfo.startTime >= 0) {
                        j = avatarObject.videoEditedInfo.startTime;
                    }
                    long startTime = j;
                    double d = avatarObject.videoEditedInfo.avatarStartTime - startTime;
                    Double.isNaN(d);
                    this.videoTimestamp = d / 1000000.0d;
                    avatarObject.videoEditedInfo.shouldLimitFps = false;
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingStarted);
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingFailed);
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileNewChunkAvailable);
                    MediaController.getInstance().scheduleVideoConvert(avatarObject, true);
                    this.uploadingImage = null;
                    ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
                    if (imageUpdaterDelegate != null) {
                        imageUpdaterDelegate.didStartUpload(true);
                    }
                } else {
                    ImageUpdaterDelegate imageUpdaterDelegate2 = this.delegate;
                    if (imageUpdaterDelegate2 != null) {
                        imageUpdaterDelegate2.didStartUpload(false);
                    }
                }
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadProgressChanged);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
                if (this.uploadingImage != null) {
                    FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingImage, false, true, 16777216);
                }
            }
            ImageUpdaterDelegate imageUpdaterDelegate3 = this.delegate;
            if (imageUpdaterDelegate3 != null) {
                imageUpdaterDelegate3.didUploadPhoto(null, null, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, null, this.bigPhoto, this.smallPhoto);
            }
        }
    }

    @Override // org.telegram.ui.PhotoCropActivity.PhotoEditActivityDelegate
    public void didFinishEdit(Bitmap bitmap) {
        processBitmap(bitmap, null);
    }

    private void cleanup() {
        this.uploadingImage = null;
        this.uploadingVideo = null;
        this.videoPath = null;
        this.convertingVideo = null;
        if (this.clearAfterUpdate) {
            this.imageReceiver.setImageBitmap((Drawable) null);
            this.parentFragment = null;
            this.delegate = null;
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        ImageUpdaterDelegate imageUpdaterDelegate;
        BaseFragment baseFragment;
        BaseFragment baseFragment2;
        if (id == NotificationCenter.fileUploaded || id == NotificationCenter.fileUploadFailed) {
            String location = (String) args[0];
            if (location.equals(this.uploadingImage)) {
                this.uploadingImage = null;
                if (id == NotificationCenter.fileUploaded) {
                    this.uploadedPhoto = (TLRPC.InputFile) args[1];
                }
            } else if (location.equals(this.uploadingVideo)) {
                this.uploadingVideo = null;
                if (id == NotificationCenter.fileUploaded) {
                    this.uploadedVideo = (TLRPC.InputFile) args[1];
                }
            } else {
                return;
            }
            if (this.uploadingImage == null && this.uploadingVideo == null && this.convertingVideo == null) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
                if (id == NotificationCenter.fileUploaded && (imageUpdaterDelegate = this.delegate) != null) {
                    imageUpdaterDelegate.didUploadPhoto(this.uploadedPhoto, this.uploadedVideo, this.videoTimestamp, this.videoPath, this.bigPhoto, this.smallPhoto);
                }
                cleanup();
            }
        } else if (id == NotificationCenter.fileUploadProgressChanged) {
            String location2 = (String) args[0];
            String path = this.convertingVideo != null ? this.uploadingVideo : this.uploadingImage;
            if (this.delegate != null && location2.equals(path)) {
                Long loadedSize = (Long) args[1];
                Long totalSize = (Long) args[2];
                float progress = Math.min(1.0f, ((float) loadedSize.longValue()) / ((float) totalSize.longValue()));
                this.delegate.onUploadProgressChanged(progress);
            }
        } else if (id == NotificationCenter.fileLoaded || id == NotificationCenter.fileLoadFailed || id == NotificationCenter.httpFileDidLoad || id == NotificationCenter.httpFileDidFailedLoad) {
            if (((String) args[0]).equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidLoad);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidFailedLoad);
                this.uploadingImage = null;
                if (id == NotificationCenter.fileLoaded || id == NotificationCenter.httpFileDidLoad) {
                    processBitmap(ImageLoader.loadBitmap(this.finalPath, null, 800.0f, 800.0f, true), null);
                } else {
                    this.imageReceiver.setImageBitmap((Drawable) null);
                }
            }
        } else if (id == NotificationCenter.filePreparingFailed) {
            MessageObject messageObject = (MessageObject) args[0];
            if (messageObject != this.convertingVideo || (baseFragment2 = this.parentFragment) == null) {
                return;
            }
            baseFragment2.getSendMessagesHelper().stopVideoService(messageObject.messageOwner.attachPath);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingStarted);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
            cleanup();
        } else if (id == NotificationCenter.fileNewChunkAvailable) {
            MessageObject messageObject2 = (MessageObject) args[0];
            if (messageObject2 != this.convertingVideo || this.parentFragment == null) {
                return;
            }
            String finalPath = (String) args[1];
            long availableSize = ((Long) args[2]).longValue();
            long finalSize = ((Long) args[3]).longValue();
            this.parentFragment.getFileLoader().checkUploadNewDataAvailable(finalPath, false, availableSize, finalSize);
            if (finalSize != 0) {
                double longValue = ((Long) args[5]).longValue();
                Double.isNaN(longValue);
                double lastFrameTimestamp = longValue / 1000000.0d;
                if (this.videoTimestamp > lastFrameTimestamp) {
                    this.videoTimestamp = lastFrameTimestamp;
                }
                Bitmap bitmap = SendMessagesHelper.createVideoThumbnailAtTime(finalPath, (long) (this.videoTimestamp * 1000.0d), null, true);
                if (bitmap != null) {
                    File path2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.smallPhoto, true);
                    if (path2 != null) {
                        path2.delete();
                    }
                    File path3 = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.bigPhoto, true);
                    if (path3 != null) {
                        path3.delete();
                    }
                    this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, (int) GroupCallActivity.TABLET_LIST_SIZE, (int) GroupCallActivity.TABLET_LIST_SIZE);
                    TLRPC.PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, 150.0f, 150.0f, 80, false, 150, 150);
                    this.smallPhoto = scaleAndSaveImage;
                    if (scaleAndSaveImage != null) {
                        try {
                            Bitmap b = BitmapFactory.decodeFile(FileLoader.getInstance(this.currentAccount).getPathToAttach(this.smallPhoto, true).getAbsolutePath());
                            String key = this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50";
                            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(b), key, true);
                        } catch (Throwable th) {
                        }
                    }
                }
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingStarted);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
                this.parentFragment.getSendMessagesHelper().stopVideoService(messageObject2.messageOwner.attachPath);
                this.videoPath = finalPath;
                this.uploadingVideo = finalPath;
                this.convertingVideo = null;
            }
        } else if (id == NotificationCenter.filePreparingStarted && ((MessageObject) args[0]) == this.convertingVideo && (baseFragment = this.parentFragment) != null) {
            this.uploadingVideo = (String) args[1];
            baseFragment.getFileLoader().uploadFile(this.uploadingVideo, false, false, (int) this.convertingVideo.videoEditedInfo.estimatedSize, ConnectionsManager.FileTypeVideo, false);
        }
    }

    public void setForceDarkTheme(boolean forceDarkTheme) {
        this.forceDarkTheme = forceDarkTheme;
    }

    public void setShowingFromDialog(boolean b) {
        this.showingFromDialog = b;
    }
}
