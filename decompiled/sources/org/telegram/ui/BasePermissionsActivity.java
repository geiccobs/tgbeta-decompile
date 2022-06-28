package org.telegram.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.camera.CameraController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes4.dex */
public class BasePermissionsActivity extends Activity {
    public static final int REQUEST_CODE_ATTACH_CONTACT = 5;
    public static final int REQUEST_CODE_CALLS = 7;
    public static final int REQUEST_CODE_EXTERNAL_STORAGE = 4;
    public static final int REQUEST_CODE_EXTERNAL_STORAGE_FOR_AVATAR = 151;
    public static final int REQUEST_CODE_GEOLOCATION = 2;
    public static final int REQUEST_CODE_OPEN_CAMERA = 20;
    public static final int REQUEST_CODE_VIDEO_MESSAGE = 150;
    protected int currentAccount = -1;

    public boolean checkPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int[] grantResults2;
        String[] permissions2;
        if (grantResults != null) {
            grantResults2 = grantResults;
        } else {
            grantResults2 = new int[0];
        }
        if (permissions != null) {
            permissions2 = permissions;
        } else {
            permissions2 = new String[0];
        }
        boolean granted = grantResults2.length > 0 && grantResults2[0] == 0;
        if (requestCode == 104) {
            if (!granted) {
                showPermissionErrorAlert(R.raw.permission_request_camera, LocaleController.getString("VoipNeedCameraPermission", R.string.VoipNeedCameraPermission));
                return true;
            } else if (GroupCallActivity.groupCallInstance != null) {
                GroupCallActivity.groupCallInstance.enableCamera();
                return true;
            } else {
                return true;
            }
        } else if (requestCode == 4 || requestCode == 151) {
            if (!granted) {
                showPermissionErrorAlert(R.raw.permission_request_folder, requestCode == 151 ? LocaleController.getString("PermissionNoStorageAvatar", R.string.PermissionNoStorageAvatar) : LocaleController.getString("PermissionStorageWithHint", R.string.PermissionStorageWithHint));
                return true;
            }
            ImageLoader.getInstance().checkMediaPaths();
            return true;
        } else if (requestCode == 5) {
            if (!granted) {
                showPermissionErrorAlert(R.raw.permission_request_contacts, LocaleController.getString("PermissionNoContactsSharing", R.string.PermissionNoContactsSharing));
                return false;
            }
            ContactsController.getInstance(this.currentAccount).forceImportContacts();
            return true;
        } else if (requestCode == 3 || requestCode == 150) {
            boolean audioGranted = true;
            boolean cameraGranted = true;
            int size = Math.min(permissions2.length, grantResults2.length);
            for (int i = 0; i < size; i++) {
                if ("android.permission.RECORD_AUDIO".equals(permissions2[i])) {
                    audioGranted = grantResults2[i] == 0;
                } else if ("android.permission.CAMERA".equals(permissions2[i])) {
                    cameraGranted = grantResults2[i] == 0;
                }
            }
            if (requestCode == 150 && (!audioGranted || !cameraGranted)) {
                showPermissionErrorAlert(R.raw.permission_request_camera, LocaleController.getString("PermissionNoCameraMicVideo", R.string.PermissionNoCameraMicVideo));
                return true;
            } else if (!audioGranted) {
                showPermissionErrorAlert(R.raw.permission_request_microphone, LocaleController.getString("PermissionNoAudioWithHint", R.string.PermissionNoAudioWithHint));
                return true;
            } else if (!cameraGranted) {
                showPermissionErrorAlert(R.raw.permission_request_camera, LocaleController.getString("PermissionNoCameraWithHint", R.string.PermissionNoCameraWithHint));
                return true;
            } else {
                if (SharedConfig.inappCamera) {
                    CameraController.getInstance().initCamera(null);
                }
                return false;
            }
        } else if (requestCode == 18 || requestCode == 19 || requestCode == 20 || requestCode == 22) {
            if (!granted) {
                showPermissionErrorAlert(R.raw.permission_request_camera, LocaleController.getString("PermissionNoCameraWithHint", R.string.PermissionNoCameraWithHint));
                return true;
            }
            return true;
        } else if (requestCode == 2) {
            NotificationCenter.getGlobalInstance().postNotificationName(granted ? NotificationCenter.locationPermissionGranted : NotificationCenter.locationPermissionDenied, new Object[0]);
            return true;
        } else {
            return true;
        }
    }

    public AlertDialog createPermissionErrorAlert(int animationId, String message) {
        return new AlertDialog.Builder(this).setTopAnimation(animationId, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).setMessage(AndroidUtilities.replaceTags(message)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.BasePermissionsActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                BasePermissionsActivity.this.m1571xa1ae6110(dialogInterface, i);
            }
        }).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), null).create();
    }

    /* renamed from: lambda$createPermissionErrorAlert$0$org-telegram-ui-BasePermissionsActivity */
    public /* synthetic */ void m1571xa1ae6110(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void showPermissionErrorAlert(int animationId, String message) {
        createPermissionErrorAlert(animationId, message).show();
    }
}
