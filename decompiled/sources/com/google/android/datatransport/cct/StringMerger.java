package com.google.android.datatransport.cct;
/* loaded from: classes3.dex */
public final class StringMerger {
    public static String mergeStrings(String part1, String part2) {
        int sizeDiff = part1.length() - part2.length();
        if (sizeDiff < 0 || sizeDiff > 1) {
            throw new IllegalArgumentException("Invalid input received");
        }
        StringBuilder url = new StringBuilder(part1.length() + part2.length());
        for (int i = 0; i < part1.length(); i++) {
            url.append(part1.charAt(i));
            if (part2.length() > i) {
                url.append(part2.charAt(i));
            }
        }
        return url.toString();
    }
}
