package com.microsoft.appcenter.distribute;

import android.content.Context;
/* loaded from: classes3.dex */
public class PermissionUtils {
    public static int[] permissionsState(Context context, String... permissions) {
        if (permissions == null) {
            return null;
        }
        int[] state = new int[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            state[i] = context.checkCallingOrSelfPermission(permissions[i]);
        }
        return state;
    }

    public static boolean permissionsAreGranted(int[] permissionsState) {
        for (int permissionState : permissionsState) {
            if (permissionState != 0) {
                return false;
            }
        }
        return true;
    }
}
