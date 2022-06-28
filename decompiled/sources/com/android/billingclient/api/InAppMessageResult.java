package com.android.billingclient.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes3.dex */
public final class InAppMessageResult {
    private final String mPurchaseToken;
    private final int mResponseCode;

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface InAppMessageResponseCode {
        public static final int NO_ACTION_NEEDED = 0;
        public static final int SUBSCRIPTION_STATUS_UPDATED = 1;
    }

    public InAppMessageResult(int i, String str) {
        this.mResponseCode = i;
        this.mPurchaseToken = str;
    }

    public String getPurchaseToken() {
        return this.mPurchaseToken;
    }

    public int getResponseCode() {
        return this.mResponseCode;
    }
}
