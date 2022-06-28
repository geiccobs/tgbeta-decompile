package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.voip.VoIPHelper;
/* loaded from: classes4.dex */
public class VoIPPermissionActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VoIPService service = VoIPService.getSharedInstance();
        boolean isVideoCall = (service == null || service.privateCall == null || !service.privateCall.video) ? false : true;
        ArrayList<String> permissions = new ArrayList<>();
        if (checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
            permissions.add("android.permission.RECORD_AUDIO");
        }
        if (isVideoCall && checkSelfPermission("android.permission.CAMERA") != 0) {
            permissions.add("android.permission.CAMERA");
        }
        if (!permissions.isEmpty()) {
            try {
                requestPermissions((String[]) permissions.toArray(new String[0]), isVideoCall ? 102 : 101);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101 || requestCode == 102) {
            boolean allGranted = true;
            int a = 0;
            while (true) {
                if (a >= grantResults.length) {
                    break;
                } else if (grantResults[a] == 0) {
                    a++;
                } else {
                    allGranted = false;
                    break;
                }
            }
            int a2 = grantResults.length;
            if (a2 > 0 && allGranted) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().acceptIncomingCall();
                }
                finish();
                startActivity(new Intent(this, LaunchActivity.class).setAction("voip"));
            } else if (!shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().declineIncomingCall();
                }
                VoIPHelper.permissionDenied(this, new Runnable() { // from class: org.telegram.ui.VoIPPermissionActivity$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPPermissionActivity.this.finish();
                    }
                }, requestCode);
            } else {
                finish();
            }
        }
    }
}
