package com.google.android.gms.internal.icing;

import android.accounts.Account;
import java.util.ArrayList;
import java.util.List;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzf {
    private List<zzk> zza;
    private String zzb;
    private boolean zzc;
    private Account zzd;

    public final zzf zza(zzk zzkVar) {
        if (this.zza == null) {
            this.zza = new ArrayList();
        }
        this.zza.add(zzkVar);
        return this;
    }

    public final zzf zzb(String str) {
        this.zzb = str;
        return this;
    }

    public final zzf zzc(boolean z) {
        this.zzc = true;
        return this;
    }

    public final zzf zzd(Account account) {
        this.zzd = account;
        return this;
    }

    public final zzg zze() {
        zzk[] zzkVarArr;
        String str = this.zzb;
        boolean z = this.zzc;
        Account account = this.zzd;
        List<zzk> list = this.zza;
        if (list != null) {
            zzkVarArr = (zzk[]) list.toArray(new zzk[list.size()]);
        } else {
            zzkVarArr = null;
        }
        return new zzg(str, z, account, zzkVarArr);
    }
}
