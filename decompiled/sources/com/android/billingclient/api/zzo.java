package com.android.billingclient.api;

import android.content.Context;
import android.content.IntentFilter;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes.dex */
public final class zzo {
    private final Context zza;
    private final zzn zzb;

    public zzo(Context context, zzbe zzbeVar) {
        this.zza = context;
        this.zzb = new zzn(this, null, null);
    }

    public final zzbe zzb() {
        zzbe unused = this.zzb.zzc;
        return null;
    }

    public final PurchasesUpdatedListener zzc() {
        PurchasesUpdatedListener purchasesUpdatedListener;
        purchasesUpdatedListener = this.zzb.zzb;
        return purchasesUpdatedListener;
    }

    public final void zze() {
        IntentFilter intentFilter = new IntentFilter("com.android.vending.billing.PURCHASES_UPDATED");
        intentFilter.addAction("com.android.vending.billing.ALTERNATIVE_BILLING");
        this.zzb.zzc(this.zza, intentFilter);
    }

    public zzo(Context context, PurchasesUpdatedListener purchasesUpdatedListener, zzc zzcVar) {
        this.zza = context;
        this.zzb = new zzn(this, purchasesUpdatedListener, zzcVar, null);
    }
}
