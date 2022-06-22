package com.stripe.android.net;

import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class StripeResponse {
    private String mResponseBody;
    private int mResponseCode;
    private Map<String, List<String>> mResponseHeaders;

    public StripeResponse(int i, String str, Map<String, List<String>> map) {
        this.mResponseCode = i;
        this.mResponseBody = str;
        this.mResponseHeaders = map;
    }

    public int getResponseCode() {
        return this.mResponseCode;
    }

    public String getResponseBody() {
        return this.mResponseBody;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return this.mResponseHeaders;
    }
}
