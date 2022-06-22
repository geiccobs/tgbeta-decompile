package com.google.android.gms.common.internal;

import android.os.IInterface;
import android.os.RemoteException;
import androidx.annotation.RecentlyNonNull;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public interface IGmsServiceBroker extends IInterface {
    void getService(@RecentlyNonNull IGmsCallbacks iGmsCallbacks, GetServiceRequest getServiceRequest) throws RemoteException;
}
