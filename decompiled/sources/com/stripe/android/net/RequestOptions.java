package com.stripe.android.net;

import com.stripe.android.util.StripeTextUtils;
/* loaded from: classes3.dex */
public class RequestOptions {
    private final String mApiVersion;
    private final String mIdempotencyKey;
    private final String mPublishableApiKey;

    private RequestOptions(String apiVersion, String idempotencyKey, String publishableApiKey) {
        this.mApiVersion = apiVersion;
        this.mIdempotencyKey = idempotencyKey;
        this.mPublishableApiKey = publishableApiKey;
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

    public static RequestOptionsBuilder builder(String publishableApiKey) {
        return new RequestOptionsBuilder(publishableApiKey);
    }

    /* loaded from: classes3.dex */
    public static final class RequestOptionsBuilder {
        private String apiVersion;
        private String idempotencyKey;
        private String publishableApiKey;

        public RequestOptionsBuilder(String publishableApiKey) {
            this.publishableApiKey = publishableApiKey;
        }

        public RequestOptionsBuilder setPublishableApiKey(String publishableApiKey) {
            this.publishableApiKey = publishableApiKey;
            return this;
        }

        public RequestOptionsBuilder setIdempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public RequestOptionsBuilder setApiVersion(String apiVersion) {
            this.apiVersion = StripeTextUtils.isBlank(apiVersion) ? null : apiVersion;
            return this;
        }

        public RequestOptions build() {
            return new RequestOptions(this.apiVersion, this.idempotencyKey, this.publishableApiKey);
        }
    }
}
