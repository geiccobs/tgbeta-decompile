package com.microsoft.appcenter.distribute;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.DeviceInfoHelper;
import com.microsoft.appcenter.utils.HashUtils;
import com.microsoft.appcenter.utils.IdHelper;
import com.microsoft.appcenter.utils.NetworkStateHelper;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.util.UUID;
import org.json.JSONException;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class DistributeUtils {
    static final String TESTER_APP_PACKAGE_NAME = "com.microsoft.hockeyapp.testerapp";

    DistributeUtils() {
    }

    public static int getNotificationId() {
        return Distribute.class.getName().hashCode();
    }

    public static int getStoredDownloadState() {
        return SharedPreferencesManager.getInt("Distribute.download_state", 0);
    }

    public static String computeReleaseHash(PackageInfo packageInfo) {
        return HashUtils.sha256(packageInfo.packageName + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + packageInfo.versionName + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + DeviceInfoHelper.getVersionCode(packageInfo));
    }

    public static void updateSetupUsingTesterApp(Activity activity, PackageInfo packageInfo) {
        String releaseHash = computeReleaseHash(packageInfo);
        String requestId = UUID.randomUUID().toString();
        String url = "ms-actesterapp://update-setup?release_hash=" + releaseHash;
        String url2 = (((url + "&redirect_id=" + activity.getPackageName()) + "&redirect_scheme=appcenter") + "&request_id=" + requestId) + "&platform=Android";
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "No token, need to open tester app to url=" + url2);
        SharedPreferencesManager.putString("Distribute.request_id", requestId);
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url2));
        intent.addFlags(268435456);
        activity.startActivity(intent);
    }

    public static void updateSetupUsingBrowser(Activity activity, String installUrl, String appSecret, PackageInfo packageInfo) {
        if (!NetworkStateHelper.getSharedInstance(activity).isNetworkConnected()) {
            AppCenterLog.info(DistributeConstants.LOG_TAG, "Postpone enabling in app updates via browser as network is disconnected.");
            Distribute.getInstance().completeWorkflow();
            return;
        }
        String releaseHash = computeReleaseHash(packageInfo);
        String requestId = UUID.randomUUID().toString();
        String url = installUrl + String.format("/apps/%s/private-update-setup/", appSecret);
        String url2 = ((((((url + "?release_hash=" + releaseHash) + "&redirect_id=" + activity.getPackageName()) + "&redirect_scheme=appcenter") + "&request_id=" + requestId) + "&platform=Android") + "&enable_failure_redirect=true") + "&install_id=" + IdHelper.getInstallId().toString();
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "No token, need to open browser to url=" + url2);
        SharedPreferencesManager.putString("Distribute.request_id", requestId);
        BrowserUtils.openBrowser(url2, activity);
    }

    public static ReleaseDetails loadCachedReleaseDetails() {
        String cachedReleaseDetails = SharedPreferencesManager.getString("Distribute.release_details");
        if (cachedReleaseDetails != null) {
            try {
                return ReleaseDetails.parse(cachedReleaseDetails);
            } catch (JSONException e) {
                AppCenterLog.error(DistributeConstants.LOG_TAG, "Invalid release details in cache.", e);
                SharedPreferencesManager.remove("Distribute.release_details");
                return null;
            }
        }
        return null;
    }

    public static boolean isInvalidUpdateTrack(int updateTrack) {
        return (updateTrack == 1 || updateTrack == 2) ? false : true;
    }
}
