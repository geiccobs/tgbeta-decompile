package com.google.android.gms.internal.phenotype;

import android.os.IBinder;
import android.os.IInterface;
/* loaded from: classes3.dex */
public class zza implements IInterface {
    private final IBinder zza;
    private final String zzb;

    public zza(IBinder iBinder, String str) {
        this.zza = iBinder;
        this.zzb = str;
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this.zza;
    }
}
