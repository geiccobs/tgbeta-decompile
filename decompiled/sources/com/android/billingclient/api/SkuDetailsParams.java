package com.android.billingclient.api;

import java.util.ArrayList;
import java.util.List;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
@Deprecated
/* loaded from: classes3.dex */
public class SkuDetailsParams {
    private String zza;
    private List zzb;

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private String zza;
        private List zzb;

        private Builder() {
        }

        /* synthetic */ Builder(zzbr zzbrVar) {
        }

        public SkuDetailsParams build() {
            String str = this.zza;
            if (str != null) {
                if (this.zzb == null) {
                    throw new IllegalArgumentException("SKU list or SkuWithOffer list must be set");
                }
                SkuDetailsParams skuDetailsParams = new SkuDetailsParams();
                skuDetailsParams.zza = str;
                skuDetailsParams.zzb = this.zzb;
                return skuDetailsParams;
            }
            throw new IllegalArgumentException("SKU type must be set");
        }

        public Builder setSkusList(List<String> list) {
            this.zzb = new ArrayList(list);
            return this;
        }

        public Builder setType(String str) {
            this.zza = str;
            return this;
        }
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    public String getSkuType() {
        return this.zza;
    }

    public List<String> getSkusList() {
        return this.zzb;
    }
}
