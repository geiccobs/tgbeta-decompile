package com.stripe.android.util;
/* loaded from: classes.dex */
public class StripeTextUtils {
    public static boolean hasAnyPrefix(String str, String... strArr) {
        if (str == null) {
            return false;
        }
        for (String str2 : strArr) {
            if (str.startsWith(str2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWholePositiveNumber(String str) {
        if (str == null) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String nullIfBlank(String str) {
        if (isBlank(str)) {
            return null;
        }
        return str;
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static String asCardBrand(String str) {
        if (isBlank(str)) {
            return null;
        }
        return "American Express".equalsIgnoreCase(str) ? "American Express" : "MasterCard".equalsIgnoreCase(str) ? "MasterCard" : "Diners Club".equalsIgnoreCase(str) ? "Diners Club" : "Discover".equalsIgnoreCase(str) ? "Discover" : "JCB".equalsIgnoreCase(str) ? "JCB" : "Visa".equalsIgnoreCase(str) ? "Visa" : "Unknown";
    }

    public static String asFundingType(String str) {
        if (isBlank(str)) {
            return null;
        }
        return "credit".equalsIgnoreCase(str) ? "credit" : "debit".equalsIgnoreCase(str) ? "debit" : "prepaid".equalsIgnoreCase(str) ? "prepaid" : "unknown";
    }

    public static String asTokenType(String str) {
        if ("card".equals(str)) {
            return "card";
        }
        return null;
    }
}
