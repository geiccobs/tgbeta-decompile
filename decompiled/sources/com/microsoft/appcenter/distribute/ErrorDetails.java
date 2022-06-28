package com.microsoft.appcenter.distribute;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes3.dex */
class ErrorDetails {
    private static final String CODE = "code";
    static final String NO_RELEASES_FOR_USER_CODE = "no_releases_for_user";
    private String code;

    ErrorDetails() {
    }

    public static ErrorDetails parse(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.code = object.getString(CODE);
        return errorDetails;
    }

    public String getCode() {
        return this.code;
    }
}
