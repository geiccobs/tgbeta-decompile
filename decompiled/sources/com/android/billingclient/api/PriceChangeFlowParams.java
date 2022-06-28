package com.android.billingclient.api;

import org.json.JSONException;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes3.dex */
public class PriceChangeFlowParams {
    private SkuDetails skuDetails;

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private SkuDetails skuDetails;

        private Builder setSkuDetails(String originalSkuDetails) {
            try {
                this.skuDetails = new SkuDetails(originalSkuDetails);
                return this;
            } catch (JSONException e) {
                throw new IllegalArgumentException("Incorrect skuDetails JSON object!", e);
            }
        }

        public PriceChangeFlowParams build() {
            SkuDetails skuDetails = this.skuDetails;
            if (skuDetails == null) {
                throw new IllegalArgumentException("SkuDetails must be set");
            }
            PriceChangeFlowParams priceChangeFlowParams = new PriceChangeFlowParams();
            priceChangeFlowParams.skuDetails = skuDetails;
            return priceChangeFlowParams;
        }

        public Builder setSkuDetails(SkuDetails skuDetails) {
            this.skuDetails = skuDetails;
            return this;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public SkuDetails getSkuDetails() {
        return this.skuDetails;
    }
}
