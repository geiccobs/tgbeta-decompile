package com.stripe.android.net;

import com.stripe.android.util.StripeJsonUtils;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
class ErrorParser {
    public static StripeError parseError(String str) {
        StripeError stripeError = new StripeError();
        try {
            JSONObject jSONObject = new JSONObject(str).getJSONObject("error");
            stripeError.charge = StripeJsonUtils.optString(jSONObject, "charge");
            stripeError.code = StripeJsonUtils.optString(jSONObject, "code");
            stripeError.decline_code = StripeJsonUtils.optString(jSONObject, "decline_code");
            stripeError.message = StripeJsonUtils.optString(jSONObject, "message");
            stripeError.param = StripeJsonUtils.optString(jSONObject, "param");
            StripeJsonUtils.optString(jSONObject, "type");
        } catch (JSONException unused) {
            stripeError.message = "An improperly formatted error response was found.";
        }
        return stripeError;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class StripeError {
        public String charge;
        public String code;
        public String decline_code;
        public String message;
        public String param;

        StripeError() {
        }
    }
}
