package com.microsoft.appcenter.ingestion.models.one;

import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class PartAUtils {
    static {
        Pattern.compile("^[a-zA-Z0-9]((\\.(?!(\\.|$)))|[_a-zA-Z0-9]){3,99}$");
    }

    public static String getTargetKey(String str) {
        return str.split("-")[0];
    }
}
