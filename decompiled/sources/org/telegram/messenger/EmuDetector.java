package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.core.content.ContextCompat;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public class EmuDetector {
    private static final String IP = "10.0.2.15";
    private static final int MIN_PROPERTIES_THRESHOLD = 5;
    private static EmuDetector mEmulatorDetector;
    private boolean detectResult;
    private boolean detected;
    private final Context mContext;
    private List<String> mListPackageName;
    private static final String[] PHONE_NUMBERS = {"15555215554", "15555215556", "15555215558", "15555215560", "15555215562", "15555215564", "15555215566", "15555215568", "15555215570", "15555215572", "15555215574", "15555215576", "15555215578", "15555215580", "15555215582", "15555215584"};
    private static final String[] DEVICE_IDS = {"000000000000000", "e21833235b6eef10", "012345678912345"};
    private static final String[] IMSI_IDS = {"310260000000000"};
    private static final String[] GENY_FILES = {"/dev/socket/genyd", "/dev/socket/baseband_genyd"};
    private static final String[] QEMU_DRIVERS = {"goldfish"};
    private static final String[] PIPES = {"/dev/socket/qemud", "/dev/qemu_pipe"};
    private static final String[] X86_FILES = {"ueventd.android_x86.rc", "x86.prop", "ueventd.ttVM_x86.rc", "init.ttVM_x86.rc", "fstab.ttVM_x86", "fstab.vbox86", "init.vbox86.rc", "ueventd.vbox86.rc"};
    private static final String[] ANDY_FILES = {"fstab.andy", "ueventd.andy.rc"};
    private static final String[] NOX_FILES = {"fstab.nox", "init.nox.rc", "ueventd.nox.rc", "/BigNoxGameHD", "/YSLauncher"};
    private static final String[] BLUE_FILES = {"/Android/data/com.bluestacks.home", "/Android/data/com.bluestacks.settings"};
    private static final Property[] PROPERTIES = {new Property("init.svc.qemud", null), new Property("init.svc.qemu-props", null), new Property("qemu.hw.mainkeys", null), new Property("qemu.sf.fake_camera", null), new Property("qemu.sf.lcd_density", null), new Property("ro.bootloader", "unknown"), new Property("ro.bootmode", "unknown"), new Property("ro.hardware", "goldfish"), new Property("ro.kernel.android.qemud", null), new Property("ro.kernel.qemu.gles", null), new Property("ro.kernel.qemu", IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE), new Property("ro.product.device", "generic"), new Property("ro.product.model", "sdk"), new Property("ro.product.name", "sdk"), new Property("ro.serialno", null)};
    private boolean isTelephony = false;
    private boolean isCheckPackage = true;

    /* loaded from: classes4.dex */
    public enum EmulatorTypes {
        GENY,
        ANDY,
        NOX,
        BLUE,
        PIPES,
        X86
    }

    /* loaded from: classes4.dex */
    public interface OnEmulatorDetectorListener {
        void onResult(boolean z);
    }

    /* loaded from: classes4.dex */
    public static class Property {
        public String name;
        public String seek_value;

        public Property(String name, String seek_value) {
            this.name = name;
            this.seek_value = seek_value;
        }
    }

    public static EmuDetector with(Context pContext) {
        if (pContext == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        if (mEmulatorDetector == null) {
            mEmulatorDetector = new EmuDetector(pContext.getApplicationContext());
        }
        return mEmulatorDetector;
    }

    private EmuDetector(Context pContext) {
        ArrayList arrayList = new ArrayList();
        this.mListPackageName = arrayList;
        this.mContext = pContext;
        arrayList.add("com.google.android.launcher.layouts.genymotion");
        this.mListPackageName.add("com.bluestacks");
        this.mListPackageName.add("com.bignox.app");
        this.mListPackageName.add("com.vphone.launcher");
    }

    public boolean isCheckTelephony() {
        return this.isTelephony;
    }

    public boolean isCheckPackage() {
        return this.isCheckPackage;
    }

    public EmuDetector setCheckTelephony(boolean telephony) {
        this.isTelephony = telephony;
        return this;
    }

    public EmuDetector setCheckPackage(boolean chkPackage) {
        this.isCheckPackage = chkPackage;
        return this;
    }

    public EmuDetector addPackageName(String pPackageName) {
        this.mListPackageName.add(pPackageName);
        return this;
    }

    public EmuDetector addPackageName(List<String> pListPackageName) {
        this.mListPackageName.addAll(pListPackageName);
        return this;
    }

    public boolean detect() {
        if (this.detected) {
            return this.detectResult;
        }
        try {
            this.detected = true;
            if (!this.detectResult) {
                this.detectResult = checkBasic();
            }
            if (!this.detectResult) {
                this.detectResult = checkAdvanced();
            }
            if (!this.detectResult) {
                this.detectResult = checkPackageName();
            }
            if (!this.detectResult) {
                this.detectResult = EmuInputDevicesDetector.detect();
            }
            return this.detectResult;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkBasic() {
        boolean z = false;
        boolean result = Build.BOARD.toLowerCase().contains("nox") || Build.BOOTLOADER.toLowerCase().contains("nox") || Build.FINGERPRINT.startsWith("generic") || Build.MODEL.toLowerCase().contains("google_sdk") || Build.MODEL.toLowerCase().contains("droid4x") || Build.MODEL.toLowerCase().contains("emulator") || Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER.toLowerCase().contains("genymotion") || Build.HARDWARE.toLowerCase().contains("goldfish") || Build.HARDWARE.toLowerCase().contains("vbox86") || Build.HARDWARE.toLowerCase().contains("android_x86") || Build.HARDWARE.toLowerCase().contains("nox") || Build.PRODUCT.equals("sdk") || Build.PRODUCT.equals("google_sdk") || Build.PRODUCT.equals("sdk_x86") || Build.PRODUCT.equals("vbox86p") || Build.PRODUCT.toLowerCase().contains("nox") || Build.SERIAL.toLowerCase().contains("nox");
        if (result) {
            return true;
        }
        if (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) {
            z = true;
        }
        boolean result2 = result | z;
        if (!result2) {
            return result2 | "google_sdk".equals(Build.PRODUCT);
        }
        return true;
    }

    private boolean checkAdvanced() {
        return checkTelephony() || checkFiles(GENY_FILES, EmulatorTypes.GENY) || checkFiles(ANDY_FILES, EmulatorTypes.ANDY) || checkFiles(NOX_FILES, EmulatorTypes.NOX) || checkFiles(BLUE_FILES, EmulatorTypes.BLUE) || checkQEmuDrivers() || checkFiles(PIPES, EmulatorTypes.PIPES) || checkIp() || (checkQEmuProps() && checkFiles(X86_FILES, EmulatorTypes.X86));
    }

    private boolean checkPackageName() {
        if (!this.isCheckPackage || this.mListPackageName.isEmpty()) {
            return false;
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        for (String pkgName : this.mListPackageName) {
            Intent tryIntent = packageManager.getLaunchIntentForPackage(pkgName);
            if (tryIntent != null) {
                List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(tryIntent, 65536);
                if (!resolveInfos.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkTelephony() {
        return ContextCompat.checkSelfPermission(this.mContext, "android.permission.READ_PHONE_STATE") == 0 && this.isTelephony && isSupportTelePhony() && (checkPhoneNumber() || checkDeviceId() || checkImsi() || checkOperatorNameAndroid());
    }

    private boolean checkPhoneNumber() {
        String[] strArr;
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        String phoneNumber = telephonyManager.getLine1Number();
        for (String number : PHONE_NUMBERS) {
            if (number.equalsIgnoreCase(phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDeviceId() {
        String[] strArr;
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        String deviceId = telephonyManager.getDeviceId();
        for (String known_deviceId : DEVICE_IDS) {
            if (known_deviceId.equalsIgnoreCase(deviceId)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkImsi() {
        String[] strArr;
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        String imsi = telephonyManager.getSubscriberId();
        for (String known_imsi : IMSI_IDS) {
            if (known_imsi.equalsIgnoreCase(imsi)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkOperatorNameAndroid() {
        String operatorName = ((TelephonyManager) this.mContext.getSystemService("phone")).getNetworkOperatorName();
        return operatorName.equalsIgnoreCase("android");
    }

    private boolean checkQEmuDrivers() {
        String[] strArr;
        File[] fileArr = {new File("/proc/tty/drivers"), new File("/proc/cpuinfo")};
        for (int i = 0; i < 2; i++) {
            File drivers_file = fileArr[i];
            if (drivers_file.exists() && drivers_file.canRead()) {
                byte[] data = new byte[1024];
                try {
                    InputStream is = new FileInputStream(drivers_file);
                    is.read(data);
                    is.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                String driver_data = new String(data);
                for (String known_qemu_driver : QEMU_DRIVERS) {
                    if (driver_data.contains(known_qemu_driver)) {
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }

    private boolean checkFiles(String[] targets, EmulatorTypes type) {
        File qemu_file;
        for (String pipe : targets) {
            if (ContextCompat.checkSelfPermission(this.mContext, "android.permission.READ_EXTERNAL_STORAGE") == 0) {
                if ((pipe.contains("/") && type == EmulatorTypes.NOX) || type == EmulatorTypes.BLUE) {
                    qemu_file = new File(Environment.getExternalStorageDirectory() + pipe);
                } else {
                    qemu_file = new File(pipe);
                }
            } else {
                qemu_file = new File(pipe);
            }
            if (qemu_file.exists()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkQEmuProps() {
        Property[] propertyArr;
        int found_props = 0;
        for (Property property : PROPERTIES) {
            String property_value = getProp(this.mContext, property.name);
            if (property.seek_value == null && property_value != null) {
                found_props++;
            }
            if (property.seek_value != null && property_value.contains(property.seek_value)) {
                found_props++;
            }
        }
        return found_props >= 5;
    }

    private boolean checkIp() {
        if (ContextCompat.checkSelfPermission(this.mContext, "android.permission.INTERNET") != 0) {
            return false;
        }
        String[] args = {"/system/bin/netcfg"};
        StringBuilder stringBuilder = new StringBuilder();
        try {
            ProcessBuilder builder = new ProcessBuilder(args);
            builder.directory(new File("/system/bin/"));
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) {
                stringBuilder.append(new String(re));
            }
            in.close();
        } catch (Exception e) {
        }
        String netData = stringBuilder.toString();
        if (TextUtils.isEmpty(netData)) {
            return false;
        }
        String[] array = netData.split("\n");
        for (String lan : array) {
            if ((lan.contains("wlan0") || lan.contains("tunl0") || lan.contains("eth0")) && lan.contains(IP)) {
                return true;
            }
        }
        return false;
    }

    private String getProp(Context context, String property) {
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class<?> systemProperties = classLoader.loadClass("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class);
            Object[] params = {property};
            return (String) get.invoke(systemProperties, params);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isSupportTelePhony() {
        PackageManager packageManager = this.mContext.getPackageManager();
        return packageManager.hasSystemFeature("android.hardware.telephony");
    }
}
