package com.google.android.gms.common.api.internal;

import android.content.Context;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.signin.zae;
import java.util.ArrayList;
import java.util.Map;
import javax.annotation.concurrent.GuardedBy;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zaag extends zaap {
    final /* synthetic */ zaaf zaa;
    private final Map<Api.Client, zaah> zab;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zaag(zaaf zaafVar, Map<Api.Client, zaah> map) {
        super(zaafVar, null);
        this.zaa = zaafVar;
        this.zab = map;
    }

    @Override // com.google.android.gms.common.api.internal.zaap
    @GuardedBy("mLock")
    public final void zaa() {
        GoogleApiAvailabilityLight googleApiAvailabilityLight;
        boolean z;
        Context context;
        zaaz zaazVar;
        zae zaeVar;
        zae zaeVar2;
        zaaz zaazVar2;
        Context context2;
        Context context3;
        boolean z2;
        googleApiAvailabilityLight = this.zaa.zad;
        com.google.android.gms.common.internal.zaj zajVar = new com.google.android.gms.common.internal.zaj(googleApiAvailabilityLight);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (Api.Client client : this.zab.keySet()) {
            if (client.requiresGooglePlayServices()) {
                z2 = this.zab.get(client).zac;
                if (!z2) {
                    arrayList.add(client);
                }
            }
            arrayList2.add(client);
        }
        int i = -1;
        int i2 = 0;
        if (arrayList.isEmpty()) {
            int size = arrayList2.size();
            while (i2 < size) {
                Object obj = arrayList2.get(i2);
                i2++;
                context3 = this.zaa.zac;
                i = zajVar.zaa(context3, (Api.Client) obj);
                if (i == 0) {
                    break;
                }
            }
        } else {
            int size2 = arrayList.size();
            while (i2 < size2) {
                Object obj2 = arrayList.get(i2);
                i2++;
                context2 = this.zaa.zac;
                i = zajVar.zaa(context2, (Api.Client) obj2);
                if (i != 0) {
                    break;
                }
            }
        }
        if (i != 0) {
            ConnectionResult connectionResult = new ConnectionResult(i, null);
            zaazVar2 = this.zaa.zaa;
            zaazVar2.zaa(new zaaj(this, this.zaa, connectionResult));
            return;
        }
        z = this.zaa.zam;
        if (z) {
            zaeVar = this.zaa.zak;
            if (zaeVar != null) {
                zaeVar2 = this.zaa.zak;
                zaeVar2.zab();
            }
        }
        for (Api.Client client2 : this.zab.keySet()) {
            zaah zaahVar = this.zab.get(client2);
            if (client2.requiresGooglePlayServices()) {
                context = this.zaa.zac;
                if (zajVar.zaa(context, client2) != 0) {
                    zaazVar = this.zaa.zaa;
                    zaazVar.zaa(new zaai(this, this.zaa, zaahVar));
                }
            }
            client2.connect(zaahVar);
        }
    }
}
