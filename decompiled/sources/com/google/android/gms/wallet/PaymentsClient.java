package com.google.android.gms.wallet;

import android.content.Context;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.internal.RemoteCall;
import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.wallet.Wallet;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes.dex */
public class PaymentsClient extends GoogleApi<Wallet.WalletOptions> {
    @RecentlyNonNull
    public Task<Boolean> isReadyToPay(@RecentlyNonNull final IsReadyToPayRequest isReadyToPayRequest) {
        return doRead(TaskApiCall.builder().setMethodKey(23705).run(new RemoteCall() { // from class: com.google.android.gms.wallet.zzab
            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                ((com.google.android.gms.internal.wallet.zzab) obj).zzr(IsReadyToPayRequest.this, (TaskCompletionSource) obj2);
            }
        }).build());
    }

    @RecentlyNonNull
    public Task<PaymentData> loadPaymentData(@RecentlyNonNull final PaymentDataRequest paymentDataRequest) {
        return doWrite(TaskApiCall.builder().run(new RemoteCall() { // from class: com.google.android.gms.wallet.zzac
            @Override // com.google.android.gms.common.api.internal.RemoteCall
            public final void accept(Object obj, Object obj2) {
                ((com.google.android.gms.internal.wallet.zzab) obj).zzs(PaymentDataRequest.this, (TaskCompletionSource) obj2);
            }
        }).setFeatures(zzj.zzc).setAutoResolveMissingFeatures(true).setMethodKey(23707).build());
    }

    public PaymentsClient(Context context, Wallet.WalletOptions walletOptions) {
        super(context, Wallet.API, walletOptions, GoogleApi.Settings.DEFAULT_SETTINGS);
    }
}
