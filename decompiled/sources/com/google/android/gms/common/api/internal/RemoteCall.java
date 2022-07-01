package com.google.android.gms.common.api.internal;

import android.os.RemoteException;
import androidx.annotation.RecentlyNonNull;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public interface RemoteCall<T, U> {
    void accept(@RecentlyNonNull T t, @RecentlyNonNull U u) throws RemoteException;
}
