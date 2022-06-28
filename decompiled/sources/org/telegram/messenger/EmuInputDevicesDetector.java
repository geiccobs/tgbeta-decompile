package org.telegram.messenger;

import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public final class EmuInputDevicesDetector {
    private static final String INPUT_DEVICES_FILE = "/proc/bus/input/devices";
    private static final String NAME_PREFIX = "N: Name=\"";
    private static final String[] RESTRICTED_DEVICES = {"bluestacks", "memuhyperv", "virtualbox"};

    private EmuInputDevicesDetector() {
    }

    public static boolean detect() {
        String[] strArr;
        List<String> deviceNames = getInputDevicesNames();
        if (deviceNames != null) {
            for (String deviceName : deviceNames) {
                for (String restrictedDeviceName : RESTRICTED_DEVICES) {
                    if (deviceName.toLowerCase().contains(restrictedDeviceName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static List<String> getInputDevicesNames() {
        File devicesFile = new File(INPUT_DEVICES_FILE);
        if (!devicesFile.canRead()) {
            return null;
        }
        try {
            List<String> lines = new ArrayList<>();
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(devicesFile)));
            while (true) {
                String line = r.readLine();
                if (line != null) {
                    if (line.startsWith(NAME_PREFIX)) {
                        String name = line.substring(NAME_PREFIX.length(), line.length() - 1);
                        if (!TextUtils.isEmpty(name)) {
                            lines.add(name);
                        }
                    }
                } else {
                    return lines;
                }
            }
        } catch (IOException e) {
            FileLog.e(e);
            return null;
        }
    }
}
