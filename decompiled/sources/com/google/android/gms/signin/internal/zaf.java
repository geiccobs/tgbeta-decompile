package com.google.android.gms.signin.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.internal.IAccountAccessor;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zaf extends com.google.android.gms.internal.base.zab implements zag {
    public zaf(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.signin.internal.ISignInService");
    }

    @Override // com.google.android.gms.signin.internal.zag
    public final void zaa(int i) throws RemoteException {
        Parcel zaa = zaa();
        zaa.writeInt(i);
        zab(7, zaa);
    }

    @Override // com.google.android.gms.signin.internal.zag
    public final void zaa(IAccountAccessor iAccountAccessor, int i, boolean z) throws RemoteException {
        Parcel zaa = zaa();
        com.google.android.gms.internal.base.zad.zaa(zaa, iAccountAccessor);
        zaa.writeInt(i);
        com.google.android.gms.internal.base.zad.zaa(zaa, z);
        zab(9, zaa);
    }

    @Override // com.google.android.gms.signin.internal.zag
    public final void zaa(zaj zajVar, zae zaeVar) throws RemoteException {
        Parcel zaa = zaa();
        com.google.android.gms.internal.base.zad.zaa(zaa, zajVar);
        com.google.android.gms.internal.base.zad.zaa(zaa, zaeVar);
        zab(12, zaa);
    }
}
