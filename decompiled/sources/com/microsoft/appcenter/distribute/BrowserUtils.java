package com.microsoft.appcenter.distribute;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import com.huawei.hms.framework.common.ContainerUtils;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.CharacterCompat;
/* loaded from: classes.dex */
class BrowserUtils {
    public static void openBrowser(String str, Activity activity) {
        try {
            openBrowserWithoutIntentChooser(str, activity);
        } catch (SecurityException e) {
            AppCenterLog.warn("AppCenterDistribute", "Browser could not be opened by trying to avoid intent chooser, starting implicit intent instead.", e);
            activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
        }
    }

    private static void openBrowserWithoutIntentChooser(String str, Activity activity) throws SecurityException {
        String str2;
        String str3;
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str));
        List<ResolveInfo> queryIntentActivities = activity.getPackageManager().queryIntentActivities(intent, 0);
        if (queryIntentActivities.isEmpty()) {
            AppCenterLog.error("AppCenterDistribute", "No browser found on device, abort login.");
            return;
        }
        ResolveInfo resolveActivity = activity.getPackageManager().resolveActivity(intent, CharacterCompat.MIN_SUPPLEMENTARY_CODE_POINT);
        String str4 = null;
        if (resolveActivity != null) {
            ActivityInfo activityInfo = resolveActivity.activityInfo;
            str2 = activityInfo.packageName;
            str3 = activityInfo.name;
            AppCenterLog.debug("AppCenterDistribute", "Default browser seems to be " + str2 + "/" + str3);
        } else {
            str3 = null;
            str2 = null;
        }
        Iterator<ResolveInfo> it = queryIntentActivities.iterator();
        while (true) {
            if (!it.hasNext()) {
                str3 = null;
                break;
            }
            ActivityInfo activityInfo2 = it.next().activityInfo;
            if (activityInfo2.packageName.equals(str2) && activityInfo2.name.equals(str3)) {
                AppCenterLog.debug("AppCenterDistribute", "And its not the picker.");
                str4 = str2;
                break;
            }
        }
        if (resolveActivity != null && str4 == null) {
            AppCenterLog.debug("AppCenterDistribute", "Default browser is actually a picker...");
        }
        if (str4 == null) {
            AppCenterLog.debug("AppCenterDistribute", "Picking first browser in list.");
            ActivityInfo activityInfo3 = queryIntentActivities.iterator().next().activityInfo;
            str4 = activityInfo3.packageName;
            str3 = activityInfo3.name;
        }
        AppCenterLog.debug("AppCenterDistribute", "Launch browser=" + str4 + "/" + str3);
        intent.setClassName(str4, str3);
        activity.startActivity(intent);
    }

    public static String appendUri(String str, String str2) throws URISyntaxException {
        URI uri = new URI(str);
        String query = uri.getQuery();
        if (query != null) {
            str2 = query + ContainerUtils.FIELD_DELIMITER + str2;
        }
        return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), str2, uri.getFragment()).toString();
    }
}
