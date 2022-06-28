package com.microsoft.appcenter.distribute;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes3.dex */
class BrowserUtils {
    BrowserUtils() {
    }

    public static void openBrowser(String url, Activity activity) {
        try {
            openBrowserWithoutIntentChooser(url, activity);
        } catch (SecurityException e) {
            AppCenterLog.warn(DistributeConstants.LOG_TAG, "Browser could not be opened by trying to avoid intent chooser, starting implicit intent instead.", e);
            activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
        }
    }

    private static void openBrowserWithoutIntentChooser(String url, Activity activity) throws SecurityException {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        List<ResolveInfo> browsers = activity.getPackageManager().queryIntentActivities(intent, 0);
        if (browsers.isEmpty()) {
            AppCenterLog.error(DistributeConstants.LOG_TAG, "No browser found on device, abort login.");
            return;
        }
        String defaultBrowserPackageName = null;
        String defaultBrowserClassName = null;
        ResolveInfo defaultBrowser = activity.getPackageManager().resolveActivity(intent, 65536);
        if (defaultBrowser != null) {
            ActivityInfo activityInfo = defaultBrowser.activityInfo;
            defaultBrowserPackageName = activityInfo.packageName;
            defaultBrowserClassName = activityInfo.name;
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Default browser seems to be " + defaultBrowserPackageName + "/" + defaultBrowserClassName);
        }
        String selectedPackageName = null;
        String selectedClassName = null;
        Iterator<ResolveInfo> it = browsers.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ResolveInfo browser = it.next();
            ActivityInfo activityInfo2 = browser.activityInfo;
            if (activityInfo2.packageName.equals(defaultBrowserPackageName) && activityInfo2.name.equals(defaultBrowserClassName)) {
                selectedPackageName = defaultBrowserPackageName;
                selectedClassName = defaultBrowserClassName;
                AppCenterLog.debug(DistributeConstants.LOG_TAG, "And its not the picker.");
                break;
            }
        }
        if (defaultBrowser != null && selectedPackageName == null) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Default browser is actually a picker...");
        }
        if (selectedPackageName == null) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Picking first browser in list.");
            ResolveInfo browser2 = browsers.iterator().next();
            ActivityInfo activityInfo3 = browser2.activityInfo;
            selectedPackageName = activityInfo3.packageName;
            selectedClassName = activityInfo3.name;
        }
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Launch browser=" + selectedPackageName + "/" + selectedClassName);
        intent.setClassName(selectedPackageName, selectedClassName);
        activity.startActivity(intent);
    }

    public static String appendUri(String uri, String appendQuery) throws URISyntaxException {
        String newQuery;
        URI oldUri = new URI(uri);
        String newQuery2 = oldUri.getQuery();
        if (newQuery2 == null) {
            newQuery = appendQuery;
        } else {
            newQuery = newQuery2 + "&" + appendQuery;
        }
        URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(), oldUri.getPath(), newQuery, oldUri.getFragment());
        return newUri.toString();
    }
}
