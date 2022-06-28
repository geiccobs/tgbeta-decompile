package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public interface ILocationSourceDelegate extends IInterface {
    void activate(zzaj zzajVar) throws RemoteException;

    void deactivate() throws RemoteException;
}
