package com.google.android.gms.wallet.callback;

import com.google.android.gms.wallet.PaymentData;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public abstract class BasePaymentDataCallbacks {
    public void onPaymentAuthorized(PaymentData paymentData, OnCompleteListener<PaymentAuthorizationResult> onCompleteListener) {
    }

    public void onPaymentDataChanged(IntermediatePaymentData intermediatePaymentData, OnCompleteListener<PaymentDataRequestUpdate> onCompleteListener) {
    }
}
