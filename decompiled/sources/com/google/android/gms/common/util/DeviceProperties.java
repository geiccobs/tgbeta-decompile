package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.RecentlyNonNull;
import com.huawei.hms.adapter.internal.AvailableCode;
import org.telegram.messenger.R;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public final class DeviceProperties {
    private static Boolean zzc;
    private static Boolean zzd;
    private static Boolean zzf;
    private static Boolean zzg;

    @TargetApi(R.styleable.MapAttrs_uiZoomControls)
    public static boolean isWearable(@RecentlyNonNull Context context) {
        return isWearable(context.getPackageManager());
    }

    @TargetApi(R.styleable.MapAttrs_uiZoomControls)
    public static boolean isWearable(@RecentlyNonNull PackageManager packageManager) {
        if (zzc == null) {
            zzc = Boolean.valueOf(PlatformVersion.isAtLeastKitKatWatch() && packageManager.hasSystemFeature("android.hardware.type.watch"));
        }
        return zzc.booleanValue();
    }

    @TargetApi(AvailableCode.ERROR_NO_ACTIVITY)
    public static boolean isWearableWithoutPlayStore(@RecentlyNonNull Context context) {
        if (isWearable(context)) {
            if (!PlatformVersion.isAtLeastN()) {
                return true;
            }
            return zzb(context) && !PlatformVersion.isAtLeastO();
        }
        return false;
    }

    @TargetApi(21)
    private static boolean zzb(Context context) {
        if (zzd == null) {
            zzd = Boolean.valueOf(PlatformVersion.isAtLeastLollipop() && context.getPackageManager().hasSystemFeature("cn.google"));
        }
        return zzd.booleanValue();
    }

    public static boolean zza(@RecentlyNonNull Context context) {
        if (zzf == null) {
            zzf = Boolean.valueOf(context.getPackageManager().hasSystemFeature("android.hardware.type.iot") || context.getPackageManager().hasSystemFeature("android.hardware.type.embedded"));
        }
        return zzf.booleanValue();
    }

    public static boolean isAuto(@RecentlyNonNull Context context) {
        return isAuto(context.getPackageManager());
    }

    public static boolean isAuto(@RecentlyNonNull PackageManager packageManager) {
        if (zzg == null) {
            zzg = Boolean.valueOf(PlatformVersion.isAtLeastO() && packageManager.hasSystemFeature("android.hardware.type.automotive"));
        }
        return zzg.booleanValue();
    }

    public static boolean isUserBuild() {
        return "user".equals(Build.TYPE);
    }
}
