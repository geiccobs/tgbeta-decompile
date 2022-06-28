package com.google.android.gms.common.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public final class DeviceProperties {
    private static Boolean zza;
    private static Boolean zzb;
    private static Boolean zzc;
    private static Boolean zzd;
    private static Boolean zze;
    private static Boolean zzf;
    private static Boolean zzg;
    private static Boolean zzh;

    private DeviceProperties() {
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x003d, code lost:
        if (com.google.android.gms.common.util.DeviceProperties.zzb.booleanValue() != false) goto L23;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isTablet(android.content.res.Resources r4) {
        /*
            r0 = 0
            if (r4 != 0) goto L4
            return r0
        L4:
            java.lang.Boolean r1 = com.google.android.gms.common.util.DeviceProperties.zza
            if (r1 != 0) goto L46
        L9:
            android.content.res.Configuration r1 = r4.getConfiguration()
            int r1 = r1.screenLayout
            r1 = r1 & 15
            r2 = 3
            r3 = 1
            if (r1 <= r2) goto L17
            r1 = 1
            goto L18
        L17:
            r1 = 0
        L18:
            if (r1 != 0) goto L3f
            java.lang.Boolean r1 = com.google.android.gms.common.util.DeviceProperties.zzb
            if (r1 != 0) goto L37
            android.content.res.Configuration r4 = r4.getConfiguration()
            int r1 = r4.screenLayout
            r1 = r1 & 15
            if (r1 > r2) goto L30
            int r4 = r4.smallestScreenWidthDp
            r1 = 600(0x258, float:8.41E-43)
            if (r4 < r1) goto L30
            r4 = 1
            goto L31
        L30:
            r4 = 0
        L31:
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r4)
            com.google.android.gms.common.util.DeviceProperties.zzb = r4
        L37:
            java.lang.Boolean r4 = com.google.android.gms.common.util.DeviceProperties.zzb
            boolean r4 = r4.booleanValue()
            if (r4 == 0) goto L40
        L3f:
            r0 = 1
        L40:
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r0)
            com.google.android.gms.common.util.DeviceProperties.zza = r4
        L46:
            java.lang.Boolean r4 = com.google.android.gms.common.util.DeviceProperties.zza
            boolean r4 = r4.booleanValue()
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.util.DeviceProperties.isTablet(android.content.res.Resources):boolean");
    }

    public static boolean isWearable(Context context) {
        return isWearable(context.getPackageManager());
    }

    public static boolean isWearable(PackageManager packageManager) {
        if (zzc == null) {
            zzc = Boolean.valueOf(PlatformVersion.isAtLeastKitKatWatch() && packageManager.hasSystemFeature("android.hardware.type.watch"));
        }
        return zzc.booleanValue();
    }

    public static boolean isWearableWithoutPlayStore(Context context) {
        if (isWearable(context)) {
            if (!PlatformVersion.isAtLeastN()) {
                return true;
            }
            return zzb(context) && !PlatformVersion.isAtLeastO();
        }
        return false;
    }

    public static boolean isSidewinder(Context context) {
        return zzb(context);
    }

    private static boolean zzb(Context context) {
        if (zzd == null) {
            zzd = Boolean.valueOf(PlatformVersion.isAtLeastLollipop() && context.getPackageManager().hasSystemFeature("cn.google"));
        }
        return zzd.booleanValue();
    }

    public static boolean isLatchsky(Context context) {
        if (zze == null) {
            PackageManager packageManager = context.getPackageManager();
            zze = Boolean.valueOf(packageManager.hasSystemFeature("com.google.android.feature.services_updater") && packageManager.hasSystemFeature("cn.google.services"));
        }
        return zze.booleanValue();
    }

    public static boolean zza(Context context) {
        if (zzf == null) {
            zzf = Boolean.valueOf(context.getPackageManager().hasSystemFeature("android.hardware.type.iot") || context.getPackageManager().hasSystemFeature("android.hardware.type.embedded"));
        }
        return zzf.booleanValue();
    }

    @Deprecated
    public static boolean isFeaturePhone(Context context) {
        return false;
    }

    public static boolean isAuto(Context context) {
        return isAuto(context.getPackageManager());
    }

    public static boolean isAuto(PackageManager packageManager) {
        if (zzg == null) {
            zzg = Boolean.valueOf(PlatformVersion.isAtLeastO() && packageManager.hasSystemFeature("android.hardware.type.automotive"));
        }
        return zzg.booleanValue();
    }

    public static boolean isTv(Context context) {
        return isTv(context.getPackageManager());
    }

    public static boolean isTv(PackageManager packageManager) {
        if (zzh == null) {
            zzh = Boolean.valueOf(packageManager.hasSystemFeature("com.google.android.tv") || packageManager.hasSystemFeature("android.hardware.type.television") || packageManager.hasSystemFeature("android.software.leanback"));
        }
        return zzh.booleanValue();
    }

    public static boolean isUserBuild() {
        return "user".equals(Build.TYPE);
    }
}
