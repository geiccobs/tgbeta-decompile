package org.telegram.ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.PhotoAlbumPickerActivity;
/* loaded from: classes5.dex */
public class WallpaperUpdater {
    private String currentPicturePath;
    private File currentWallpaperPath;
    private WallpaperUpdaterDelegate delegate;
    private Activity parentActivity;
    private BaseFragment parentFragment;
    private File picturePath = null;

    /* loaded from: classes5.dex */
    public interface WallpaperUpdaterDelegate {
        void didSelectWallpaper(File file, Bitmap bitmap, boolean z);

        void needOpenColorPicker();
    }

    public WallpaperUpdater(Activity activity, BaseFragment fragment, WallpaperUpdaterDelegate wallpaperUpdaterDelegate) {
        this.parentActivity = activity;
        this.parentFragment = fragment;
        this.delegate = wallpaperUpdaterDelegate;
    }

    public void showAlert(final boolean fromTheme) {
        int[] icons;
        CharSequence[] items;
        BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
        builder.setTitle(LocaleController.getString("ChoosePhoto", R.string.ChoosePhoto), true);
        if (fromTheme) {
            items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto), LocaleController.getString("SelectFromGallery", R.string.SelectFromGallery), LocaleController.getString("SelectColor", R.string.SelectColor), LocaleController.getString("Default", R.string.Default)};
            icons = null;
        } else {
            items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto), LocaleController.getString("SelectFromGallery", R.string.SelectFromGallery)};
            icons = new int[]{R.drawable.msg_camera, R.drawable.msg_photos};
        }
        builder.setItems(items, icons, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.WallpaperUpdater$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                WallpaperUpdater.this.m3208lambda$showAlert$0$orgtelegramuiComponentsWallpaperUpdater(fromTheme, dialogInterface, i);
            }
        });
        builder.show();
    }

    /* renamed from: lambda$showAlert$0$org-telegram-ui-Components-WallpaperUpdater */
    public /* synthetic */ void m3208lambda$showAlert$0$orgtelegramuiComponentsWallpaperUpdater(boolean fromTheme, DialogInterface dialogInterface, int i) {
        try {
            if (i == 0) {
                try {
                    Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File image = AndroidUtilities.generatePicturePath();
                    if (image != null) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            takePictureIntent.putExtra("output", FileProvider.getUriForFile(this.parentActivity, "org.telegram.messenger.beta.provider", image));
                            takePictureIntent.addFlags(2);
                            takePictureIntent.addFlags(1);
                        } else {
                            takePictureIntent.putExtra("output", Uri.fromFile(image));
                        }
                        this.currentPicturePath = image.getAbsolutePath();
                    }
                    this.parentActivity.startActivityForResult(takePictureIntent, 10);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else if (i == 1) {
                openGallery();
            } else if (fromTheme) {
                if (i == 2) {
                    this.delegate.needOpenColorPicker();
                } else if (i == 3) {
                    this.delegate.didSelectWallpaper(null, null, false);
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public void openGallery() {
        if (this.parentFragment != null) {
            if (Build.VERSION.SDK_INT >= 23 && this.parentFragment.getParentActivity() != null && this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                return;
            }
            PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(PhotoAlbumPickerActivity.SELECT_TYPE_WALLPAPER, false, false, null);
            fragment.setAllowSearchImages(false);
            fragment.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() { // from class: org.telegram.ui.Components.WallpaperUpdater.1
                @Override // org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate
                public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate) {
                    WallpaperUpdater.this.didSelectPhotos(photos);
                }

                @Override // org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate
                public void startPhotoSelectActivity() {
                    try {
                        Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                        photoPickerIntent.setType("image/*");
                        WallpaperUpdater.this.parentActivity.startActivityForResult(photoPickerIntent, 11);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            });
            this.parentFragment.presentFragment(fragment);
            return;
        }
        Intent photoPickerIntent = new Intent("android.intent.action.PICK");
        photoPickerIntent.setType("image/*");
        this.parentActivity.startActivityForResult(photoPickerIntent, 11);
    }

    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos) {
        try {
            if (!photos.isEmpty()) {
                SendMessagesHelper.SendingMediaInfo info = photos.get(0);
                if (info.path != null) {
                    File directory = FileLoader.getDirectory(4);
                    this.currentWallpaperPath = new File(directory, Utilities.random.nextInt() + ".jpg");
                    android.graphics.Point screenSize = AndroidUtilities.getRealScreenSize();
                    Bitmap bitmap = ImageLoader.loadBitmap(info.path, null, (float) screenSize.x, (float) screenSize.y, true);
                    FileOutputStream stream = new FileOutputStream(this.currentWallpaperPath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                    this.delegate.didSelectWallpaper(this.currentWallpaperPath, bitmap, true);
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public void cleanup() {
    }

    public String getCurrentPicturePath() {
        return this.currentPicturePath;
    }

    public void setCurrentPicturePath(String value) {
        this.currentPicturePath = value;
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:10:0x005f -> B:32:0x006f). Please submit an issue!!! */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            if (requestCode == 10) {
                AndroidUtilities.addMediaToGallery(this.currentPicturePath);
                FileOutputStream stream = null;
                try {
                    try {
                        try {
                            File directory = FileLoader.getDirectory(4);
                            this.currentWallpaperPath = new File(directory, Utilities.random.nextInt() + ".jpg");
                            android.graphics.Point screenSize = AndroidUtilities.getRealScreenSize();
                            Bitmap bitmap = ImageLoader.loadBitmap(this.currentPicturePath, null, (float) screenSize.x, (float) screenSize.y, true);
                            stream = new FileOutputStream(this.currentWallpaperPath);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                            this.delegate.didSelectWallpaper(this.currentWallpaperPath, bitmap, false);
                            stream.close();
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    } catch (Exception e2) {
                        FileLog.e(e2);
                        if (stream != null) {
                            stream.close();
                        }
                    }
                    this.currentPicturePath = null;
                } catch (Throwable th) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Exception e3) {
                            FileLog.e(e3);
                        }
                    }
                    throw th;
                }
            } else if (requestCode != 11 || data == null || data.getData() == null) {
            } else {
                try {
                    File directory2 = FileLoader.getDirectory(4);
                    this.currentWallpaperPath = new File(directory2, Utilities.random.nextInt() + ".jpg");
                    android.graphics.Point screenSize2 = AndroidUtilities.getRealScreenSize();
                    Bitmap bitmap2 = ImageLoader.loadBitmap(null, data.getData(), (float) screenSize2.x, (float) screenSize2.y, true);
                    bitmap2.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(this.currentWallpaperPath));
                    this.delegate.didSelectWallpaper(this.currentWallpaperPath, bitmap2, false);
                } catch (Exception e4) {
                    FileLog.e(e4);
                }
            }
        }
    }
}
