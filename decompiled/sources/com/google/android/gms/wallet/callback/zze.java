package com.google.android.gms.wallet.callback;

import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
final class zze implements OnCompleteListener<PaymentAuthorizationResult> {
    private final OnCompleteListener<CallbackOutput> zza;

    public zze(OnCompleteListener<CallbackOutput> onCompleteListener) {
        this.zza = onCompleteListener;
    }

    /* renamed from: zza */
    public final synchronized void complete(PaymentAuthorizationResult paymentAuthorizationResult) {
        OnCompleteListener<CallbackOutput> onCompleteListener = this.zza;
        zzj zza = CallbackOutput.zza();
        CallbackOutput callbackOutput = zza.zza;
        callbackOutput.zza = 1;
        callbackOutput.zzb = 1;
        byte[] serializeToBytes = SafeParcelableSerializer.serializeToBytes(paymentAuthorizationResult);
        CallbackOutput callbackOutput2 = zza.zza;
        callbackOutput2.zzc = serializeToBytes;
        onCompleteListener.complete(callbackOutput2);
    }
}
