package com.google.android.gms.common.internal.service;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zaaa;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zaj extends com.google.android.gms.internal.base.zab implements zak {
    public zaj(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.common.internal.service.IClientTelemetryService");
    }

    @Override // com.google.android.gms.common.internal.service.zak
    public final void zaa(zaaa zaaaVar) throws RemoteException {
        Parcel zaa = zaa();
        com.google.android.gms.internal.base.zad.zaa(zaa, zaaaVar);
        zac(1, zaa);
    }
}
