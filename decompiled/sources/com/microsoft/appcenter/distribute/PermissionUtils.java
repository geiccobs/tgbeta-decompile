package com.microsoft.appcenter.distribute;

import android.content.Context;
/* loaded from: classes.dex */
public class PermissionUtils {
    public static int[] permissionsState(Context context, String... strArr) {
        if (strArr == null) {
            return null;
        }
        int[] iArr = new int[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            iArr[i] = context.checkCallingOrSelfPermission(strArr[i]);
        }
        return iArr;
    }

    public static boolean permissionsAreGranted(int[] iArr) {
        for (int i : iArr) {
            if (i != 0) {
                return false;
            }
        }
        return true;
    }
}
