package com.google.firebase.appindexing.internal;

import android.os.RemoteException;
import com.google.android.gms.internal.icing.zzaa;
import com.google.android.gms.internal.icing.zzak;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes.dex */
public final class zzq extends zzs {
    final /* synthetic */ zzc[] zza;

    public zzq(zzt zztVar, zzc[] zzcVarArr) {
        this.zza = zzcVarArr;
    }

    @Override // com.google.firebase.appindexing.internal.zzs
    protected final void zza(zzaa zzaaVar) throws RemoteException {
        zzaaVar.zze(new zzak(this), this.zza);
    }
}
