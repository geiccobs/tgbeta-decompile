package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.ConnectionResult;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zacg implements Runnable {
    private final /* synthetic */ zace zaa;

    public zacg(zace zaceVar) {
        this.zaa = zaceVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        zach zachVar;
        zachVar = this.zaa.zah;
        zachVar.zaa(new ConnectionResult(4));
    }
}
