package com.google.android.gms.common.util;

import java.util.regex.Pattern;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public class Strings {
    public static boolean isEmptyOrWhitespace(String str) {
        return str == null || str.trim().isEmpty();
    }

    static {
        Pattern.compile("\\$\\{(.*?)\\}");
    }
}
