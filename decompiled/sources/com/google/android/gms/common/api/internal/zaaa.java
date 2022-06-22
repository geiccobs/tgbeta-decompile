package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
import java.util.Set;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zaaa implements zaaw {
    private final zaaz zaa;
    private boolean zab = false;

    public zaaa(zaaz zaazVar) {
        this.zaa = zaazVar;
    }

    @Override // com.google.android.gms.common.api.internal.zaaw
    public final void zaa() {
    }

    @Override // com.google.android.gms.common.api.internal.zaaw
    public final void zaa(Bundle bundle) {
    }

    @Override // com.google.android.gms.common.api.internal.zaaw
    public final void zaa(ConnectionResult connectionResult, Api<?> api, boolean z) {
    }

    @Override // com.google.android.gms.common.api.internal.zaaw
    public final <A extends Api.AnyClient, R extends Result, T extends BaseImplementation$ApiMethodImpl<R, A>> T zaa(T t) {
        return (T) zab(t);
    }

    @Override // com.google.android.gms.common.api.internal.zaaw
    public final <A extends Api.AnyClient, T extends BaseImplementation$ApiMethodImpl<? extends Result, A>> T zab(T t) {
        try {
            this.zaa.zad.zae.zaa(t);
            zaar zaarVar = this.zaa.zad;
            Api.Client client = zaarVar.zab.get(t.getClientKey());
            Preconditions.checkNotNull(client, "Appropriate Api was not requested.");
            if (!client.isConnected() && this.zaa.zab.containsKey(t.getClientKey())) {
                t.setFailedResult(new Status(17));
            } else {
                t.run(client);
            }
        } catch (DeadObjectException unused) {
            this.zaa.zaa(new zaad(this, this));
        }
        return t;
    }

    @Override // com.google.android.gms.common.api.internal.zaaw
    public final boolean zab() {
        if (this.zab) {
            return false;
        }
        Set<zacn> set = this.zaa.zad.zad;
        if (set != null && !set.isEmpty()) {
            this.zab = true;
            for (zacn zacnVar : set) {
                zacnVar.zaa();
            }
            return false;
        }
        this.zaa.zaa((ConnectionResult) null);
        return true;
    }

    @Override // com.google.android.gms.common.api.internal.zaaw
    public final void zac() {
        if (this.zab) {
            this.zab = false;
            this.zaa.zaa(new zaac(this, this));
        }
    }

    @Override // com.google.android.gms.common.api.internal.zaaw
    public final void zaa(int i) {
        this.zaa.zaa((ConnectionResult) null);
        this.zaa.zae.zaa(i, this.zab);
    }

    public final void zad() {
        if (this.zab) {
            this.zab = false;
            this.zaa.zad.zae.zaa();
            zab();
        }
    }
}
