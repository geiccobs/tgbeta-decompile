package com.microsoft.appcenter.distribute;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public class InstallerUtils {
    private static final Set<String> LOCAL_STORES;
    private static Boolean sInstalledFromAppStore;

    static {
        HashSet hashSet = new HashSet();
        LOCAL_STORES = hashSet;
        hashSet.add("adb");
        hashSet.add("com.android.packageinstaller");
        hashSet.add("com.google.android.packageinstaller");
        hashSet.add("com.android.managedprovisioning");
        hashSet.add("com.miui.packageinstaller");
        hashSet.add("com.samsung.android.packageinstaller");
        hashSet.add("pc");
        hashSet.add("com.google.android.apps.nbu.files");
        hashSet.add("org.mozilla.firefox");
        hashSet.add("com.android.chrome");
    }

    public static Intent getInstallIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.INSTALL_PACKAGE");
        intent.setData(uri);
        intent.addFlags(1);
        intent.addFlags(268435456);
        intent.addFlags(536870912);
        return intent;
    }

    public static synchronized boolean isInstalledFromAppStore(String str, Context context) {
        boolean booleanValue;
        synchronized (InstallerUtils.class) {
            if (sInstalledFromAppStore == null) {
                String installerPackageName = context.getPackageManager().getInstallerPackageName(context.getPackageName());
                AppCenterLog.debug(str, "InstallerPackageName=" + installerPackageName);
                sInstalledFromAppStore = Boolean.valueOf(installerPackageName != null && !LOCAL_STORES.contains(installerPackageName));
            }
            booleanValue = sInstalledFromAppStore.booleanValue();
        }
        return booleanValue;
    }

    public static boolean isUnknownSourcesEnabled(Context context) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 26) {
            return context.getApplicationInfo().targetSdkVersion < 26 || context.getPackageManager().canRequestPackageInstalls();
        } else if (i >= 17 && i < 21) {
            return "1".equals(Settings.Global.getString(context.getContentResolver(), "install_non_market_apps"));
        } else {
            return "1".equals(Settings.Secure.getString(context.getContentResolver(), "install_non_market_apps"));
        }
    }
}
