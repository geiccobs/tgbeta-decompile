package com.microsoft.appcenter.distribute;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes3.dex */
public class InstallerUtils {
    static final String INSTALL_NON_MARKET_APPS_ENABLED = "1";
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

    InstallerUtils() {
    }

    public static Intent getInstallIntent(Uri fileUri) {
        Intent intent = new Intent("android.intent.action.INSTALL_PACKAGE");
        intent.setData(fileUri);
        intent.addFlags(1);
        intent.addFlags(268435456);
        intent.addFlags(536870912);
        return intent;
    }

    public static synchronized boolean isInstalledFromAppStore(String logTag, Context context) {
        boolean booleanValue;
        synchronized (InstallerUtils.class) {
            if (sInstalledFromAppStore == null) {
                String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
                AppCenterLog.debug(logTag, "InstallerPackageName=" + installer);
                sInstalledFromAppStore = Boolean.valueOf(installer != null && !LOCAL_STORES.contains(installer));
            }
            booleanValue = sInstalledFromAppStore.booleanValue();
        }
        return booleanValue;
    }

    public static boolean isUnknownSourcesEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            return context.getApplicationInfo().targetSdkVersion < 26 || context.getPackageManager().canRequestPackageInstalls();
        } else if (Build.VERSION.SDK_INT >= 17 && Build.VERSION.SDK_INT < 21) {
            return "1".equals(Settings.Global.getString(context.getContentResolver(), "install_non_market_apps"));
        } else {
            return "1".equals(Settings.Secure.getString(context.getContentResolver(), "install_non_market_apps"));
        }
    }
}
