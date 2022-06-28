package com.android.billingclient.api;

import java.util.List;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes3.dex */
public final class QueryProductDetailsParams {
    private final com.google.android.gms.internal.play_billing.zzu zza;

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private com.google.android.gms.internal.play_billing.zzu zza;

        private Builder() {
        }

        /* synthetic */ Builder(zzbj zzbjVar) {
        }

        public QueryProductDetailsParams build() {
            return new QueryProductDetailsParams(this, null);
        }

        public Builder setProductList(List<Product> list) {
            if (list == null || list.isEmpty()) {
                throw new IllegalArgumentException("Product list cannot be empty.");
            }
            boolean z = false;
            boolean z2 = false;
            for (Product product : list) {
                z |= product.zzb().equals("inapp");
                z2 |= product.zzb().equals("subs");
            }
            if (!z || !z2) {
                this.zza = com.google.android.gms.internal.play_billing.zzu.zzk(list);
                return this;
            }
            throw new IllegalArgumentException("All products should be of the same product type.");
        }
    }

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    /* loaded from: classes3.dex */
    public static class Product {
        private final String zza;
        private final String zzb;

        /* compiled from: com.android.billingclient:billing@@5.0.0 */
        /* loaded from: classes3.dex */
        public static class Builder {
            private String zza;
            private String zzb;

            private Builder() {
            }

            /* synthetic */ Builder(zzbk zzbkVar) {
            }

            public Product build() {
                if (this.zza != null) {
                    if (this.zzb == null) {
                        throw new IllegalArgumentException("Product type must be provided.");
                    }
                    return new Product(this, null);
                }
                throw new IllegalArgumentException("Product id must be provided.");
            }

            public Builder setProductId(String str) {
                this.zza = str;
                return this;
            }

            public Builder setProductType(String str) {
                this.zzb = str;
                return this;
            }
        }

        /* synthetic */ Product(Builder builder, zzbl zzblVar) {
            this.zza = builder.zza;
            this.zzb = builder.zzb;
        }

        public static Builder newBuilder() {
            return new Builder(null);
        }

        public final String zza() {
            return this.zza;
        }

        public final String zzb() {
            return this.zzb;
        }
    }

    /* synthetic */ QueryProductDetailsParams(Builder builder, zzbm zzbmVar) {
        this.zza = builder.zza;
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    public final com.google.android.gms.internal.play_billing.zzu zza() {
        return this.zza;
    }

    public final String zzb() {
        return ((Product) this.zza.get(0)).zzb();
    }
}
