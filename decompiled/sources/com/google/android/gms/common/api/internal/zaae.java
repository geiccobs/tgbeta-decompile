package com.google.android.gms.common.api.internal;

import android.content.Context;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zaae implements Runnable {
    private final /* synthetic */ zaaf zaa;

    public zaae(zaaf zaafVar) {
        this.zaa = zaafVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        GoogleApiAvailabilityLight googleApiAvailabilityLight;
        Context context;
        googleApiAvailabilityLight = this.zaa.zad;
        context = this.zaa.zac;
        googleApiAvailabilityLight.cancelAvailabilityErrorNotifications(context);
    }
}
