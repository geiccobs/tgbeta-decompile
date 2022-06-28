package com.google.android.gms.wallet.callback;

import android.content.Intent;
import android.os.IBinder;
import com.google.android.gms.wallet.PaymentData;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public abstract class BasePaymentDataCallbacksService extends zzd {
    protected abstract BasePaymentDataCallbacks createPaymentDataCallbacks();

    @Override // com.google.android.gms.wallet.callback.zzd, android.app.Service
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override // com.google.android.gms.wallet.callback.zzd, android.app.Service
    public void onCreate() {
        super.onCreate();
    }

    @Override // com.google.android.gms.wallet.callback.zzd
    public final void onRunTask(String str, CallbackInput callbackInput, OnCompleteListener<CallbackOutput> onCompleteListener) {
        if (callbackInput.getCallbackType() != 0) {
            BasePaymentDataCallbacks createPaymentDataCallbacks = createPaymentDataCallbacks();
            if (callbackInput.getCallbackType() == 1) {
                createPaymentDataCallbacks.onPaymentAuthorized((PaymentData) callbackInput.deserializeRequest(PaymentData.CREATOR), new zze(onCompleteListener));
                return;
            } else if (callbackInput.getCallbackType() != 2) {
                throw new IllegalStateException("Unknown Callback Types");
            } else {
                createPaymentDataCallbacks.onPaymentDataChanged((IntermediatePaymentData) callbackInput.deserializeRequest(IntermediatePaymentData.CREATOR), new zzf(onCompleteListener));
                return;
            }
        }
        throw new IllegalStateException("Callback Types must be set");
    }
}
