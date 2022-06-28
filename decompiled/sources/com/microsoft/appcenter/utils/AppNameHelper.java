package com.microsoft.appcenter.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
/* loaded from: classes3.dex */
public class AppNameHelper {
    public static String getAppName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int labelRes = applicationInfo.labelRes;
        if (labelRes == 0) {
            return String.valueOf(applicationInfo.nonLocalizedLabel);
        }
        return context.getString(labelRes);
    }
}
