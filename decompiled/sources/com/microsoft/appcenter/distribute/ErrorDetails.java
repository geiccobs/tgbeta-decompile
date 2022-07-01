package com.microsoft.appcenter.distribute;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
class ErrorDetails {
    private String code;

    ErrorDetails() {
    }

    public static ErrorDetails parse(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.code = jSONObject.getString("code");
        return errorDetails;
    }

    public String getCode() {
        return this.code;
    }
}
