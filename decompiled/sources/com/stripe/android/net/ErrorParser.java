package com.stripe.android.net;

import com.stripe.android.util.StripeJsonUtils;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes3.dex */
class ErrorParser {
    private static final String FIELD_CHARGE = "charge";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_DECLINE_CODE = "decline_code";
    private static final String FIELD_ERROR = "error";
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_PARAM = "param";
    private static final String FIELD_TYPE = "type";
    static final String MALFORMED_RESPONSE_MESSAGE = "An improperly formatted error response was found.";

    ErrorParser() {
    }

    public static StripeError parseError(String rawError) {
        StripeError stripeError = new StripeError();
        try {
            JSONObject jsonError = new JSONObject(rawError);
            JSONObject errorObject = jsonError.getJSONObject("error");
            stripeError.charge = StripeJsonUtils.optString(errorObject, FIELD_CHARGE);
            stripeError.code = StripeJsonUtils.optString(errorObject, FIELD_CODE);
            stripeError.decline_code = StripeJsonUtils.optString(errorObject, FIELD_DECLINE_CODE);
            stripeError.message = StripeJsonUtils.optString(errorObject, FIELD_MESSAGE);
            stripeError.param = StripeJsonUtils.optString(errorObject, FIELD_PARAM);
            stripeError.type = StripeJsonUtils.optString(errorObject, "type");
        } catch (JSONException e) {
            stripeError.message = MALFORMED_RESPONSE_MESSAGE;
        }
        return stripeError;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class StripeError {
        public String charge;
        public String code;
        public String decline_code;
        public String message;
        public String param;
        public String type;

        StripeError() {
        }
    }
}
