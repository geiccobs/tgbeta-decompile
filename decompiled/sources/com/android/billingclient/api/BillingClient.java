package com.android.billingclient.api;

import android.app.Activity;
import android.content.Context;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes.dex */
public abstract class BillingClient {

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    /* loaded from: classes.dex */
    public static final class Builder {
        private volatile boolean zzb;
        private final Context zzc;
        private volatile PurchasesUpdatedListener zzd;

        /* synthetic */ Builder(Context context, zzp zzpVar) {
            this.zzc = context;
        }

        public BillingClient build() {
            if (this.zzc != null) {
                if (this.zzd == null) {
                    throw new IllegalArgumentException("Please provide a valid listener for purchases updates.");
                }
                if (this.zzb) {
                    if (this.zzd != null) {
                        return new BillingClientImpl(null, this.zzb, this.zzc, this.zzd, null);
                    }
                    return new BillingClientImpl(null, this.zzb, this.zzc, null);
                }
                throw new IllegalArgumentException("Support for pending purchases must be enabled. Enable this by calling 'enablePendingPurchases()' on BillingClientBuilder.");
            }
            throw new IllegalArgumentException("Please provide a valid Context.");
        }

        public Builder enablePendingPurchases() {
            this.zzb = true;
            return this;
        }

        public Builder setListener(PurchasesUpdatedListener purchasesUpdatedListener) {
            this.zzd = purchasesUpdatedListener;
            return this;
        }
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context, null);
    }

    public abstract boolean isReady();

    public abstract BillingResult launchBillingFlow(Activity activity, BillingFlowParams billingFlowParams);

    public abstract void queryProductDetailsAsync(QueryProductDetailsParams queryProductDetailsParams, ProductDetailsResponseListener productDetailsResponseListener);

    public abstract void queryPurchasesAsync(QueryPurchasesParams queryPurchasesParams, PurchasesResponseListener purchasesResponseListener);

    public abstract void startConnection(BillingClientStateListener billingClientStateListener);
}
