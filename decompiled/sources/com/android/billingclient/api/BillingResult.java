package com.android.billingclient.api;

import com.google.android.gms.internal.play_billing.zzb;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes.dex */
public final class BillingResult {
    private int zza;
    private String zzb;

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    /* loaded from: classes.dex */
    public static class Builder {
        private int zza;
        private String zzb = "";

        /* synthetic */ Builder(zzba zzbaVar) {
        }

        public BillingResult build() {
            BillingResult billingResult = new BillingResult();
            billingResult.zza = this.zza;
            billingResult.zzb = this.zzb;
            return billingResult;
        }

        public Builder setDebugMessage(String str) {
            this.zzb = str;
            return this;
        }

        public Builder setResponseCode(int i) {
            this.zza = i;
            return this;
        }
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    public int getResponseCode() {
        return this.zza;
    }

    public String toString() {
        String zzl = zzb.zzl(this.zza);
        String str = this.zzb;
        return "Response Code: " + zzl + ", Debug Message: " + str;
    }
}
