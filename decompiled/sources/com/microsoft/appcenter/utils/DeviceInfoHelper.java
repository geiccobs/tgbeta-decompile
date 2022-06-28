package com.microsoft.appcenter.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.google.firebase.messaging.Constants;
import com.microsoft.appcenter.ingestion.models.Device;
import com.microsoft.appcenter.ingestion.models.WrapperSdk;
import java.util.Locale;
import java.util.TimeZone;
/* loaded from: classes3.dex */
public class DeviceInfoHelper {
    private static final String OS_NAME = "Android";
    private static WrapperSdk sWrapperSdk;

    public static synchronized Device getDeviceInfo(Context context) throws DeviceInfoException {
        Device device;
        synchronized (DeviceInfoHelper.class) {
            device = new Device();
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
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
                device.setOsName(OS_NAME);
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

    private static String getScreenSize(Context context) {
        Display defaultDisplay;
        int screenWidth;
        int screenHeight;
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            DisplayManager displayManager = (DisplayManager) context.getSystemService(Constants.ScionAnalytics.MessageType.DISPLAY_NOTIFICATION);
            defaultDisplay = displayManager.getDisplay(0);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            size.x = displayMetrics.widthPixels;
            size.y = displayMetrics.heightPixels;
        } else {
            defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
            defaultDisplay.getSize(size);
        }
        switch (defaultDisplay.getRotation()) {
            case 1:
            case 3:
                screenHeight = size.x;
                screenWidth = size.y;
                break;
            case 2:
            default:
                screenWidth = size.x;
                screenHeight = size.y;
                break;
        }
        return screenWidth + "x" + screenHeight;
    }

    public static synchronized void setWrapperSdk(WrapperSdk wrapperSdk) {
        synchronized (DeviceInfoHelper.class) {
            sWrapperSdk = wrapperSdk;
        }
    }

    /* loaded from: classes3.dex */
    public static class DeviceInfoException extends Exception {
        public DeviceInfoException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }
}
