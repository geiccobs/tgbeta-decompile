package com.google.android.gms.internal.identity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import com.google.android.exoplayer2.C;
import com.google.android.gms.common.ConnectionResult;
/* loaded from: classes3.dex */
public final class zzf extends zzh {
    private Activity mActivity;
    private final int zzj;

    public zzf(int i, Activity activity) {
        this.zzj = i;
        this.mActivity = activity;
    }

    public final void setActivity(Activity activity) {
        this.mActivity = null;
    }

    @Override // com.google.android.gms.internal.identity.zzg
    public final void zza(int i, Bundle bundle) {
        String str;
        if (i == 1) {
            Intent intent = new Intent();
            intent.putExtras(bundle);
            PendingIntent createPendingResult = this.mActivity.createPendingResult(this.zzj, intent, C.BUFFER_FLAG_ENCRYPTED);
            if (createPendingResult == null) {
                return;
            }
            try {
                createPendingResult.send(1);
                return;
            } catch (PendingIntent.CanceledException e) {
                e = e;
                str = "Exception settng pending result";
            }
        } else {
            PendingIntent pendingIntent = null;
            if (bundle != null) {
                pendingIntent = (PendingIntent) bundle.getParcelable("com.google.android.gms.identity.intents.EXTRA_PENDING_INTENT");
            }
            ConnectionResult connectionResult = new ConnectionResult(i, pendingIntent);
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this.mActivity, this.zzj);
                    return;
                } catch (IntentSender.SendIntentException e2) {
                    e = e2;
                    str = "Exception starting pending intent";
                }
            } else {
                try {
                    PendingIntent createPendingResult2 = this.mActivity.createPendingResult(this.zzj, new Intent(), C.BUFFER_FLAG_ENCRYPTED);
                    if (createPendingResult2 == null) {
                        return;
                    }
                    createPendingResult2.send(1);
                    return;
                } catch (PendingIntent.CanceledException e3) {
                    e = e3;
                    str = "Exception setting pending result";
                }
            }
        }
        Log.w("AddressClientImpl", str, e);
    }
}
