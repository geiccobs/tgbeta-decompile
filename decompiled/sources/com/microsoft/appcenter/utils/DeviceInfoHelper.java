package com.microsoft.appcenter.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.microsoft.appcenter.ingestion.models.Device;
import com.microsoft.appcenter.ingestion.models.WrapperSdk;
import java.util.Locale;
import java.util.TimeZone;
/* loaded from: classes.dex */
public class DeviceInfoHelper {
    private static WrapperSdk sWrapperSdk;

    public static synchronized Device getDeviceInfo(Context context) throws DeviceInfoException {
        Device device;
        synchronized (DeviceInfoHelper.class) {
            device = new Device();
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                device.setAppVersion(packageInfo.versionName);
                device.setAppBuild(String.valueOf(getVersionCode(packageInfo)));
                device.setAppNamespace(context.getPackageName());
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                    String networkCountryIso = telephonyManager.getNetworkCountryIso();
                    if (!TextUtils.isEmpty(networkCountryIso)) {
                        device.setCarrierCountry(networkCountryIso);
                    }
                    String networkOperatorName = telephonyManager.getNetworkOperatorName();
                    if (!TextUtils.isEmpty(networkOperatorName)) {
                        device.setCarrierName(networkOperatorName);
                    }
                } catch (Exception e) {
                    AppCenterLog.error("AppCenter", "Cannot retrieve carrier info", e);
                }
                device.setLocale(Locale.getDefault().toString());
                device.setModel(Build.MODEL);
                device.setOemName(Build.MANUFACTURER);
                device.setOsApiLevel(Integer.valueOf(Build.VERSION.SDK_INT));
                device.setOsName("Android");
                device.setOsVersion(Build.VERSION.RELEASE);
                device.setOsBuild(Build.ID);
                try {
                    device.setScreenSize(getScreenSize(context));
                } catch (Exception e2) {
                    AppCenterLog.error("AppCenter", "Cannot retrieve screen size", e2);
                }
                device.setSdkName("appcenter.android");
                device.setSdkVersion("3.3.1");
                device.setTimeZoneOffset(Integer.valueOf((TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 60) / 1000));
                WrapperSdk wrapperSdk = sWrapperSdk;
                if (wrapperSdk != null) {
                    device.setWrapperSdkVersion(wrapperSdk.getWrapperSdkVersion());
                    device.setWrapperSdkName(sWrapperSdk.getWrapperSdkName());
                    device.setWrapperRuntimeVersion(sWrapperSdk.getWrapperRuntimeVersion());
                    device.setLiveUpdateReleaseLabel(sWrapperSdk.getLiveUpdateReleaseLabel());
                    device.setLiveUpdateDeploymentKey(sWrapperSdk.getLiveUpdateDeploymentKey());
                    device.setLiveUpdatePackageHash(sWrapperSdk.getLiveUpdatePackageHash());
                }
            } catch (Exception e3) {
                AppCenterLog.error("AppCenter", "Cannot retrieve package info", e3);
                throw new DeviceInfoException("Cannot retrieve package info", e3);
            }
        }
        return device;
    }

    public static int getVersionCode(PackageInfo packageInfo) {
        return packageInfo.versionCode;
    }

    @SuppressLint({"SwitchIntDef"})
    private static String getScreenSize(Context context) {
        Display display;
        int i;
        int i2;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            display = ((DisplayManager) context.getSystemService("display")).getDisplay(0);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            point.x = displayMetrics.widthPixels;
            point.y = displayMetrics.heightPixels;
        } else {
            display = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
            display.getSize(point);
        }
        int rotation = display.getRotation();
        if (rotation == 1 || rotation == 3) {
            int i3 = point.x;
            int i4 = point.y;
            i2 = i3;
            i = i4;
        } else {
            i = point.x;
            i2 = point.y;
        }
        return i + "x" + i2;
    }

    /* loaded from: classes.dex */
    public static class DeviceInfoException extends Exception {
        public DeviceInfoException(String str, Throwable th) {
            super(str, th);
        }
    }
}
