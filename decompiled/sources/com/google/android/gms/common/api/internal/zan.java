package com.google.android.gms.common.api.internal;

import android.app.PendingIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.internal.Preconditions;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zan implements Runnable {
    final /* synthetic */ zal zaa;
    private final zak zab;

    public zan(zal zalVar, zak zakVar) {
        this.zaa = zalVar;
        this.zab = zakVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (!this.zaa.zaa) {
            return;
        }
        ConnectionResult zab = this.zab.zab();
        if (zab.hasResolution()) {
            this.zaa.mLifecycleFragment.startActivityForResult(GoogleApiActivity.zaa(this.zaa.getActivity(), (PendingIntent) Preconditions.checkNotNull(zab.getResolution()), this.zab.zaa(), false), 1);
        } else if (this.zaa.zac.getErrorResolutionIntent(this.zaa.getActivity(), zab.getErrorCode(), null) != null) {
            this.zaa.zac.zaa(this.zaa.getActivity(), this.zaa.mLifecycleFragment, zab.getErrorCode(), 2, this.zaa);
        } else if (zab.getErrorCode() == 18) {
            this.zaa.zac.zaa(this.zaa.getActivity().getApplicationContext(), new zam(this, GoogleApiAvailability.zaa(this.zaa.getActivity(), this.zaa)));
        } else {
            this.zaa.zaa(zab, this.zab.zaa());
        }
    }
}
