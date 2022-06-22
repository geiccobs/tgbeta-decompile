package com.android.billingclient.api;

import java.util.List;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes.dex */
public interface ProductDetailsResponseListener {
    void onProductDetailsResponse(BillingResult billingResult, List<ProductDetails> list);
}
