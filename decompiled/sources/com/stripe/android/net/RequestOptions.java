package com.stripe.android.net;
/* loaded from: classes.dex */
public class RequestOptions {
    private final String mApiVersion;
    private final String mIdempotencyKey;
    private final String mPublishableApiKey;

    private RequestOptions(String str, String str2, String str3) {
        this.mApiVersion = str;
        this.mIdempotencyKey = str2;
        this.mPublishableApiKey = str3;
    }

    public String getApiVersion() {
        return this.mApiVersion;
    }

    public String getIdempotencyKey() {
        return this.mIdempotencyKey;
    }

    public String getPublishableApiKey() {
        return this.mPublishableApiKey;
    }

    public static RequestOptionsBuilder builder(String str) {
        return new RequestOptionsBuilder(str);
    }

    /* loaded from: classes.dex */
    public static final class RequestOptionsBuilder {
        private String apiVersion;
        private String idempotencyKey;
        private String publishableApiKey;

        public RequestOptionsBuilder(String str) {
            this.publishableApiKey = str;
        }

        public RequestOptions build() {
            return new RequestOptions(this.apiVersion, this.idempotencyKey, this.publishableApiKey);
        }
    }
}
