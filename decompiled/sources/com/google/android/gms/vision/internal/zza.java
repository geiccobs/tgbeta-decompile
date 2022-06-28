package com.google.android.gms.vision.internal;

import android.os.RemoteException;
import com.google.android.gms.common.util.PlatformVersion;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zza {
    public static RemoteException zza(String str) {
        if (PlatformVersion.isAtLeastIceCreamSandwichMR1()) {
            return new RemoteException(str);
        }
        return new RemoteException();
    }
}
