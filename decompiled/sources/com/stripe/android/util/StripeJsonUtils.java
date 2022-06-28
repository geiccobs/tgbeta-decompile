package com.stripe.android.util;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes3.dex */
public class StripeJsonUtils {
    static final String EMPTY = "";
    static final String NULL = "null";

    public static String getString(JSONObject jsonObject, String fieldName) throws JSONException {
        return nullIfNullOrEmpty(jsonObject.getString(fieldName));
    }

    public static String optString(JSONObject jsonObject, String fieldName) {
        return nullIfNullOrEmpty(jsonObject.optString(fieldName));
    }

    static String nullIfNullOrEmpty(String possibleNull) {
        if (NULL.equals(possibleNull) || "".equals(possibleNull)) {
            return null;
        }
        return possibleNull;
    }
}
