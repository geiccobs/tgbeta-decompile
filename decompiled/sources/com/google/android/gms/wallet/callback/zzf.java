package com.google.android.gms.wallet.callback;

import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
final class zzf implements OnCompleteListener<PaymentDataRequestUpdate> {
    private final OnCompleteListener<CallbackOutput> zza;

    public zzf(OnCompleteListener<CallbackOutput> onCompleteListener) {
        this.zza = onCompleteListener;
    }

    /* renamed from: zza */
    public final synchronized void complete(PaymentDataRequestUpdate paymentDataRequestUpdate) {
        OnCompleteListener<CallbackOutput> onCompleteListener = this.zza;
        zzj zza = CallbackOutput.zza();
        CallbackOutput callbackOutput = zza.zza;
        callbackOutput.zza = 2;
        callbackOutput.zzb = 1;
        byte[] serializeToBytes = SafeParcelableSerializer.serializeToBytes(paymentDataRequestUpdate);
        CallbackOutput callbackOutput2 = zza.zza;
        callbackOutput2.zzc = serializeToBytes;
        onCompleteListener.complete(callbackOutput2);
    }
}
