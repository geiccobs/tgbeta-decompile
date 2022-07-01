package com.google.android.gms.common.api.internal;

import android.app.Dialog;
import android.app.PendingIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.internal.Preconditions;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
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
            zal zalVar = this.zaa;
            zalVar.mLifecycleFragment.startActivityForResult(GoogleApiActivity.zaa(zalVar.getActivity(), (PendingIntent) Preconditions.checkNotNull(zab.getResolution()), this.zab.zaa(), false), 1);
            return;
        }
        zal zalVar2 = this.zaa;
        if (zalVar2.zac.getErrorResolutionIntent(zalVar2.getActivity(), zab.getErrorCode(), null) != null) {
            zal zalVar3 = this.zaa;
            zalVar3.zac.zaa(zalVar3.getActivity(), this.zaa.mLifecycleFragment, zab.getErrorCode(), 2, this.zaa);
        } else if (zab.getErrorCode() == 18) {
            Dialog zaa = GoogleApiAvailability.zaa(this.zaa.getActivity(), this.zaa);
            zal zalVar4 = this.zaa;
            zalVar4.zac.zaa(zalVar4.getActivity().getApplicationContext(), new zam(this, zaa));
        } else {
            this.zaa.zaa(zab, this.zab.zaa());
        }
    }
}
