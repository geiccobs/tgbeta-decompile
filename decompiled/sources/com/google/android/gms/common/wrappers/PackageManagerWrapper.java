package com.google.android.gms.common.wrappers;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.util.PlatformVersion;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public class PackageManagerWrapper {
    private final Context zza;

    public PackageManagerWrapper(@RecentlyNonNull Context context) {
        this.zza = context;
    }

    @RecentlyNonNull
    public ApplicationInfo getApplicationInfo(@RecentlyNonNull String str, int i) throws PackageManager.NameNotFoundException {
        return this.zza.getPackageManager().getApplicationInfo(str, i);
    }

    @RecentlyNonNull
    public PackageInfo getPackageInfo(@RecentlyNonNull String str, int i) throws PackageManager.NameNotFoundException {
        return this.zza.getPackageManager().getPackageInfo(str, i);
    }

    @TargetApi(19)
    public final boolean zza(int i, @RecentlyNonNull String str) {
        if (PlatformVersion.isAtLeastKitKat()) {
            try {
                AppOpsManager appOpsManager = (AppOpsManager) this.zza.getSystemService("appops");
                if (appOpsManager == null) {
                    throw new NullPointerException("context.getSystemService(Context.APP_OPS_SERVICE) is null");
                }
                appOpsManager.checkPackage(i, str);
                return true;
            } catch (SecurityException unused) {
                return false;
            }
        }
        String[] packagesForUid = this.zza.getPackageManager().getPackagesForUid(i);
        if (str != null && packagesForUid != null) {
            for (String str2 : packagesForUid) {
                if (str.equals(str2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int checkCallingOrSelfPermission(@RecentlyNonNull String str) {
        return this.zza.checkCallingOrSelfPermission(str);
    }

    public int checkPermission(@RecentlyNonNull String str, @RecentlyNonNull String str2) {
        return this.zza.getPackageManager().checkPermission(str, str2);
    }

    @RecentlyNonNull
    public CharSequence getApplicationLabel(@RecentlyNonNull String str) throws PackageManager.NameNotFoundException {
        return this.zza.getPackageManager().getApplicationLabel(this.zza.getPackageManager().getApplicationInfo(str, 0));
    }
}
