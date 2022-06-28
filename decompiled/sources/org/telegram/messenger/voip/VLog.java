package org.telegram.messenger.voip;

import android.text.TextUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
/* loaded from: classes4.dex */
class VLog {
    public static native void d(String str);

    public static native void e(String str);

    public static native void i(String str);

    public static native void v(String str);

    public static native void w(String str);

    VLog() {
    }

    public static void e(Throwable x) {
        e(null, x);
    }

    public static void e(String msg, Throwable x) {
        StringWriter sw = new StringWriter();
        if (!TextUtils.isEmpty(msg)) {
            sw.append((CharSequence) msg);
            sw.append((CharSequence) ": ");
        }
        PrintWriter pw = new PrintWriter(sw);
        x.printStackTrace(pw);
        String[] lines = sw.toString().split("\n");
        for (String line : lines) {
            e(line);
        }
    }
}
