package com.google.android.gms.common.internal.service;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zaaa;
import com.google.android.gms.internal.base.zab;
import com.google.android.gms.internal.base.zad;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zaj extends zab implements zak {
    public zaj(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.common.internal.service.IClientTelemetryService");
    }

    @Override // com.google.android.gms.common.internal.service.zak
    public final void zaa(zaaa zaaaVar) throws RemoteException {
        Parcel zaa = zaa();
        zad.zaa(zaa, zaaaVar);
        zac(1, zaa);
    }
}
